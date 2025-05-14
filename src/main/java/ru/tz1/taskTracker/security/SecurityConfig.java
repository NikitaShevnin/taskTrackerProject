package ru.tz1.taskTracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tz1.taskTracker.util.JwtUtil;

import java.io.IOException;

/**
 * <h2>Конфигурация безопасности приложения</h2>
 * <p>Регулирует доступ к URL-адресам. Этот класс настраивает фильтрацию, аутентификацию и обработку доступа,
 * включая добавление JwtAuthenticationFilter перед фильтром для аутентификации по имени и паролю.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * <h3>Сервис пользовательских данных</h3>
     * <p>Сервис для получения данных пользователя при аутентификации.</p>
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * <h3>Секретный ключ JWT</h3>
     * <p>Секретный ключ для JWT токенов.</p>
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * <h3>JWT фильтр аутентификации</h3>
     * <p>Создает и настраивает фильтр JWT аутентификации.</p>
     *
     * @return Настроенный JWT фильтр.
     */
    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(secretKey, userDetailsService, jwtUtil);
    }

    /**
     * <h3>Цепочка фильтров безопасности</h3>
     * <p>Определяет цепочку фильтров безопасности.</p>
     *
     * @param http Объект HttpSecurity для настройки фильтров безопасности.
     * @return Настроенная SecurityFilterChain.
     * @throws Exception При возникновении ошибок настройки.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF, так как приложение использует JWT (без состояния)
                .csrf(csrf -> csrf.disable())
                // Настраиваем управление сессиями как STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Настраиваем авторизацию запросов
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**", "/h2-console/**", "/favicon.ico", "/error", "/tasks.html", "/mainPage").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Дополнительная настройка для H2 консоли (если используется)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * <h3>Кодировщик паролей</h3>
     * <p>Определяет менеджер паролей, использующий BCrypt для хеширования паролей.</p>
     *
     * @return Объект PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * <h3>Менеджер аутентификации</h3>
     * <p>Создает и настраивает менеджер аутентификации.</p>
     *
     * @param http Объект HttpSecurity для получения SharedObject.
     * @return Настроенный AuthenticationManager.
     * @throws Exception При возникновении ошибок настройки.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        configureAuthentication(authenticationManagerBuilder);
        return authenticationManagerBuilder.build();
    }

    /**
     * <h3>Настройка аутентификации</h3>
     * <p>Конфигурирует аутентификацию, создавая пользователей в памяти.</p>
     *
     * @param auth Объект AuthenticationManagerBuilder для настройки.
     * @throws Exception При возникновении ошибок настройки.
     */
    private void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("password")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
    }

    /**
     * <h3>Обработка неавторизованного доступа</h3>
     * <p>Обрабатывает неавторизованный доступ, отправляя соответствующий код ошибки.</p>
     *
     * @param request  HTTP запрос.
     * @param response HTTP ответ.
     * @throws IOException При возникновении ошибок ввода/вывода.
     */
    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    /**
     * <h3>Обработка запрещенного доступа</h3>
     * <p>Обрабатывает доступ запрещен, отправляя соответствующий код ошибки.</p>
     *
     * @param request  HTTP запрос.
     * @param response HTTP ответ.
     * @throws IOException При возникновении ошибок ввода/вывода.
     */
    private void handleAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }
}