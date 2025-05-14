package ru.tz1.taskTracker.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <h1>Утилита для работы с JSON Web Token (JWT)</h1>
 * <p>Предоставляет методы для генерации, валидации и извлечения информации из токенов.</p>
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /**
     * <h2>Генерация токена</h2>
     * <p>Генерирует JWT для указанного email.</p>
     *
     * @param email Электронная почта пользователя, для которого создается токен
     * @return Сгенерированный JWT в виде строки
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * <h2>Проверка валидности токена</h2>
     * <p>Проверяет валидность токена, сравнивая его с указанной электронной почтой.</p>
     *
     * @param token    Токен для проверки
     * @param username Электронная почта пользователя, к которому относится токен
     * @return {@code true}, если токен валиден, иначе {@code false}
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * <h2>Извлечение имени пользователя</h2>
     * <p>Извлекает электронную почту (субъект) из токена.</p>
     *
     * @param token JWT, из которого нужно извлечь электронную почту
     * @return Электронная почта, содержащаяся в токене
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * <h2>Извлечение утверждений</h2>
     * <p>Извлекает все утверждения (claims) из токена.</p>
     *
     * @param token JWT, из которого нужно извлечь утверждения
     * @return Объект Claims, содержащий данные токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * <h2>Проверка истечения срока токена</h2>
     * <p>Проверяет, истек ли токен.</p>
     *
     * @param token JWT для проверки
     * @return {@code true}, если токен истек, иначе {@code false}
     */
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}