package ru.tz1.taskTracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;

/**
 * Конфигурация безопасности для приложения с использованием Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Настройка цепочки фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки безопасности.
     * @return построенная цепочка фильтров безопасности.
     * @throws Exception при возникновении ошибок во время настройки.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureHttpSecurity(http); // Настройка параметров HttpSecurity
        return http.build(); // Построение цепочки фильтров
    }

    /**
     * Метод для настройки разрешений и поведения фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки безопасности.
     * @throws Exception при возникновении ошибок во время настройки.
     */
    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()  // Разрешить доступ к логину
                        .requestMatchers("/api/auth/register").permitAll() // Разрешить доступ к регистрации
                        .requestMatchers("/favicon.ico").permitAll() // Разрешить доступ к favicon
                        .requestMatchers("/error").permitAll() // Разрешить доступ к странице ошибки
                        .requestMatchers("/h2-console/**").permitAll() // Разрешить доступ к H2 консоли
                        .requestMatchers("/tasks/**").hasRole("USER") // Доступ для пользователей
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Доступ для администраторов
                        .anyRequest().authenticated() // Все другие запросы требуют аутентификации
                )
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/register") // Отключение CSRF для указанных маршрутов
                        .ignoringRequestMatchers("/h2-console/**") // Отключение CSRF для H2 консоли
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            handleUnauthorizedAccess(request, response); // Обработка несанкционированного доступа
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            handleAccessDenied(request, response); // Обработка доступа с отказом
                        })
                );

        // Настройка заголовков
        http.headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts.disable()) // Отключение HSTS
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:8080")) // Использование нового способа
        );
    }

    /**
     * Метод для обработки несанкционированного доступа.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @throws IOException при возникновении ошибок ввода/вывода.
     */
    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Unauthorized access attempt to: " + request.getRequestURI()); // Логируем URL, к которому был несанкционированный доступ
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); // Возвращаем ошибку 401
    }

    /**
     * Метод для обработки доступа с отказом.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @throws IOException при возникновении ошибок ввода/вывода.
     */
    private void handleAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Access denied to: " + request.getRequestURI()); // Логируем URL, к которому был отказ в доступе
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied"); // Возвращаем ошибку 403
    }

    /**
     * Объявление бина для кодирования паролей.
     *
     * @return PasswordEncoder для хеширования паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настройка менеджера аутентификации.
     *
     * @param http объект HttpSecurity для получения AuthenticationManagerBuilder.
     * @return сконфигурированный AuthenticationManager.
     * @throws Exception при возникновении ошибок во время настройки.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        configureAuthentication(authenticationManagerBuilder); // Конфигурация аутентификации
        return authenticationManagerBuilder.build(); // Возвращаем сконфигурированный AuthenticationManager
    }

    /**
     * Метод для настройки аутентификации пользователей.
     *
     * @param auth объект AuthenticationManagerBuilder для настройки аутентификации.
     * @throws Exception при возникновении ошибок во время настройки.
     */
    private void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        // Настройка встроенной аутентификации с пользователями в памяти
        auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder().encode("password")).roles("USER") // Пользователь "user"
                .and()
                .withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN"); // Пользователь "admin"
    }
}