package ru.tz1.taskTracker.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для обработки входящих HTTP-запросов и аутентификации пользователей на основе JWT токена.
 * Проверяет наличие заголовка Authorization с токеном, извлекает данные пользователя из токена,
 * и сохраняет аутентификацию в SecurityContextHolder, если токен действителен.
 * Если токен недействителен, возвращает статус 401 - Unauthorized.
 */
public class JwtAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final String secretKey; // Секретный ключ для валидации JWT
    private final UserDetailsService userDetailsService; // Сервис для загрузки данных пользователя

    /**
     * Конструктор фильтра, инициализирующий секретный ключ и сервис пользователя.
     *
     * @param secretKey Секретный ключ для валидации токенов.
     * @param userDetailsService Сервис для загрузки данных пользователя.
     */
    @Autowired
    public JwtAuthenticationFilter(String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }


    /**
     * Метод инициализации фильтра.
     * Вызывается при создании фильтра, может использоваться для настройки необходимых параметров.
     *
     * @param filterConfig Конфигурация фильтра.
     * @throws ServletException Если возникает ошибка при инициализации.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Initializing JwtAuthenticationFilter.");
    }

    /**
     * Метод фильтрации, который вызывается для обработки входящих HTTP-запросов.
     * Проверяет наличие токена JWT в заголовке Authorization и выполняет аутентификацию пользователя.
     *
     * @param request Входящий HTTP-запрос.
     * @param response Исходящий HTTP-ответ.
     * @param chain Цепочка фильтров для обработки запроса.
     * @throws IOException При ошибках ввода-вывода.
     * @throws ServletException При ошибках сервлета.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Приводим request и response к специфичным типам
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Вызываем метод специфичного doFilter
        doFilter(httpRequest, httpResponse, chain);
    }

    /**
     * Основной метод фильтрации, выполняющий проверку токена JWT и аутентификацию пользователя.
     *
     * @param request Входящий HTTP-запрос.
     * @param response Исходящий HTTP-ответ.
     * @param chain Цепочка фильтров.
     * @throws IOException При ошибках ввода-вывода.
     * @throws ServletException При ошибках сервлета.
     */
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Логируем вход в метод doFilter с URI запроса
        logger.info("Entering doFilter for request: {}", request.getRequestURI());

        // Получаем заголовок Authorization из запроса
        String authHeader = request.getHeader("Authorization");
        // Логируем значение заголовка Authorization
        logger.debug("Authorization Header is: {}", authHeader);

        // Проверяем, содержит ли заголовок Authorization токен, начинающийся с "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Извлекаем токен, убирая часть "Bearer "
            String token = authHeader.substring(7);
            // Логируем извлеченный токен
            logger.info("Extracted Token: {}", token);
            try {
                // Парсим токен, используя секретный ключ, и извлекаем его данные (Claims)
                var claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
                // Получаем имя пользователя из токена
                String username = claims.getSubject();

                // Проверяем, что имя пользователя не является null
                if (username != null) {
                    // Логируем аутентификацию пользователя
                    logger.info("Authenticating user: {}", username);
                    // Загружаем детали пользователя по имени
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // Создаем объект аутентификации для хранения информации о пользователе
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Логируем успешную аутентификацию
                    logger.debug("Authentication successful for user: {}", username);
                    // Сохраняем аутентификацию в SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Логируем, если имя пользователя извлеченное из токена равно null
                    logger.debug("Username extracted from token is null.");
                }
            } catch (JwtException e) {
                // Ловим исключение, если валидация токена не удалась
                logger.debug("Token validation failed: {}", e.getMessage());
                // Устанавливаем статус ответа как 401 - Unauthorized
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Выходим из метода, не продолжая обработку
            }
        }

        // Продолжаем выполнение фильтров в цепочке
        chain.doFilter(request, response);

        // Логируем выход из метода doFilter с URI запроса
        logger.info("Exiting doFilter for request: {}", request.getRequestURI()); // Логируем выход
    }

    /**
     * Метод освобождения ресурсов, используемых фильтром.
     * Вызывается при уничтожении фильтра.
     */
    @Override
    public void destroy() {
        logger.debug("Destroying JwtAuthenticationFilter.");
    }
}