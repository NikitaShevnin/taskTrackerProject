package ru.tz1.taskTracker.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.service.UserService;
import ru.tz1.taskTracker.util.JwtUtil;

/**
 * Контроллер для обработки аутентификации пользователей.
 * Предоставляет методы для входа в систему и регистрации пользователей.
 */
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Метод для входа пользователя в систему.
     *
     * @param user объект пользователя с данными для аутентификации, должен быть действительным
     * @return ResponseEntity, содержащий JWT-токен и информацию о пользователе, если аутентификация прошла успешно,
     *         или сообщение об ошибке в противном случае.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid User user) {
        logger.info("Login attempt for user: {}", user.getEmail());

        // Находим пользователя по email
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser == null) {
            logger.warn("User not found: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }

        // Логируем пароли для отладки
        logger.debug("Raw Password: {}", user.getPassword());
        logger.debug("Encoded Password from DB: {}", existingUser.getPassword());

        // Проверка пароля
        if (!userService.isPasswordValid(user.getPassword(), existingUser.getPassword())) {
            logger.warn("Invalid password for user: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }

        // Генерация и логирование JWT-токена
        logger.info("Generating token for user: {}", existingUser.getEmail());
        String token = jwtUtil.generateToken(existingUser.getEmail());
        logger.info("User {} successfully logged in", user.getEmail());

        // Возвращаем токен, роль и URL для перенаправления
        return ResponseEntity.ok(new LoginResponse(token, existingUser.getRole(), "/mainPage"));
    }

    /**
     * Метод для отображения страницы входа в систему.
     *
     * @return имя страницы для отображения.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "authenticationPage";
    }

    /**
     * Метод для отображения страницы регистрации.
     *
     * @return имя страницы для отображения.
     */
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registerNewUser";
    }

    /**
     * Вложенный класс для представления успешного ответа на вход в систему.
     */
    public static class LoginResponse {
        private final String token;
        private final String role;
        private final String redirectUrl;

        public LoginResponse(String token, String role, String redirectUrl) {
            this.token = token;
            this.role = role;
            this.redirectUrl = redirectUrl;
        }

        public String getToken() {
            return token;
        }

        public String getRole() {
            return role;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }
    }

    /**
     * Вложенный класс для представления сообщения об ошибке.
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}