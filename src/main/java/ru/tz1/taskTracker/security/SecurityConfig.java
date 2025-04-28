package ru.tz1.taskTracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.IOException;

/**
 * Конфигурация безопасности приложения, регулирующая доступ к URL-адресам.
 * Этот класс настраивает фильтрацию, аутентификацию и обработку доступа,
 * включая добавление JwtAuthenticationFilter перед фильтром для аутентификации по имени и паролю.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String secretKey = "WorkSecretKey";


    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Определяет цепочку фильтров безопасности.
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
                        // Разрешаем доступ к эндпоинтам аутентификации и H2 консоли
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Регистрируем JWT фильтр перед стандартным фильтром аутентификации
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Дополнительная настройка для H2 консоли (если используется)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Определяет менеджер паролей, использующий BCrypt для хеширования паролей.
     *
     * @return Объект PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настраивает правила доступа к различным URL-адресам и параметры безопасности.
     *
     * @param http Объект HttpSecurity для настройки.
     * @throws Exception При возникновении ошибок настройки.
     */
    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/tasks/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/register")
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            handleUnauthorizedAccess(request, response);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            handleAccessDenied(request, response);
                        })
                );

        http.headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts.disable())
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:8080"))
        );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Создает экземпляр JwtAuthenticationFilter с указанным секретным ключом и сервисом пользователей.
     *
     * @return Настроенный JwtAuthenticationFilter.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(secretKey, userDetailsService);
    }

    /**
     * Обрабатывает случаи несанкционированного доступа, отправляя статус 401.
     *
     * @param request Входящий HTTP-запрос.
     * @param response Исходящий HTTP-ответ.
     * @throws IOException При ошибках ввода-вывода.
     */
    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    /**
     * Обрабатывает случаи отказа в доступе, отправляя статус 403.
     *
     * @param request Входящий HTTP-запрос.
     * @param response Исходящий HTTP-ответ.
     * @throws IOException При ошибках ввода-вывода.
     */
    private void handleAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    /**
     * Создает менеджер аутентификации и настраивает пользователей для аутентификации в памяти.
     *
     * @param http Объект HttpSecurity для настройки.
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
     * Конфигурирует аутентификацию, создавая пользователей в памяти.
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
}