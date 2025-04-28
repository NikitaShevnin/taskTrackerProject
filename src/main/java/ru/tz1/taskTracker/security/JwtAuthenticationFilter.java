package ru.tz1.taskTracker.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final String secretKey;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Entering doFilter for request: {}", request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization Header is: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                logger.warn("Bearer token is empty");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            logger.info("Extracted Token: {}", token);
            try {
                var claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.info("Authenticating user: {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication successful for user: {}", username);
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
