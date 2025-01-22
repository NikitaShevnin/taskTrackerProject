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

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
        return ResponseEntity.ok(new LoginResponse(token, existingUser.getRole(), "/tasks"));
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "authenticationPage";
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registerNewUser";
    }

    public static class LoginResponse {
        private String token;
        private String role;
        private String redirectUrl;

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
