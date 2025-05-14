package ru.tz1.taskTracker.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tz1.taskTracker.util.JwtUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final String secretKey;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(String secretKey, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Пропускаем публичные эндпоинты
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Entering doFilter for request: {}", request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization Header is: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("Token received from frontend");
            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                logger.warn("Bearer token is empty");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            logger.info("Extracted Token: {}", token);
            logger.info("Sending token to backend for verification");
            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.info("Authenticating user: {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Authentication successful for user: {}", username);
                        logger.info("Token successfully verified by backend");
                    }
                } else if (username == null) {
                    logger.warn("Username extracted from token is null.");
                }
            } catch (JwtException | IllegalArgumentException e) {
                logger.error("Token validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            logger.debug("Authorization header is missing or does not start with Bearer");
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
        logger.info("Exiting doFilter for request: {}", request.getRequestURI());
    }
}
