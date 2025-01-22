package ru.tz1.taskTracker.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Этот фильтр обрабатывает входящие HTTP-запросы,
 * проверяет наличие заголовка Authorization с токеном, а затем пытается извлечь данные пользователя.
 * Если токен действителен, он сохраняет аутентификацию в SecurityContextHolder. Если токен недействителен,
 * возвращает статус 401 - Unauthorized.
 */
public class JwtAuthenticationFilter implements Filter {
    private final String secretKey;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Можно использовать для инициализации ресурсов при создании фильтра
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Приводим request и response к специфичным типам
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Вызываем метод специфичного doFilter
        doFilter(httpRequest, httpResponse, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                var claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
                String username = claims.getSubject();

                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Освобождение ресурсов, если это требуется при уничтожении фильтра
    }
}