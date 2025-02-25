package ru.tz1.taskTracker.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Утилита для работы с JSON Web Token (JWT).
 * Предоставляет методы для генерации, валидации и извлечения информации из токенов.
 */
@Component
public class JwtUtil {

    private final String SECRET_KEY = "secret"; // Замените на безопасный ключ!

    /**
     * Генерирует JWT для указанного email.
     *
     * @param email Электронная почта пользователя, для которого создается токен.
     * @return Сгенерированный JWT в виде строки.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Устанавливаем субъект токена (email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Устанавливаем дату выдачи токена
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Устанавливаем дату истечения (10 часов)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Подписываем токен с использованием алгоритма HS256 и секретного ключа
                .compact(); // Компактный формат токена
    }

    /**
     * Проверяет валидность токена, сравнивая его с указанной электронной почтой.
     *
     * @param token Токен для проверки.
     * @param email Электронная почта пользователя, к которому относится токен.
     * @return true, если токен валиден, иначе false.
     */
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractUsername(token); // Извлекаем email из токена
        return (extractedEmail.equals(email) && !isTokenExpired(token)); // Проверяем совпадение email и истечение токена
    }

    /**
     * Извлекает электронную почту (субъект) из токена.
     *
     * @param token JWT, из которого нужно извлечь электронную почту.
     * @return Электронная почта, содержащаяся в токене.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject(); // Извлекаем все данные токена и получаем субъект
    }

    /**
     * Извлекает все утверждения (claims) из токена.
     *
     * @param token JWT, из которого нужно извлечь утверждения.
     * @return Объект Claims, содержащий данные токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody(); // Парсим токен и возвращаем тело (claims)
    }

    /**
     * Проверяет, истек ли токен.
     *
     * @param token JWT для проверки.
     * @return true, если токен истек, иначе false.
     */
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date()); // Проверяем дату истечения токена
    }
}