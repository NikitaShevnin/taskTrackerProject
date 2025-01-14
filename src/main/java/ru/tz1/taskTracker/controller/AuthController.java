package ru.tz1.taskTracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        logger.info("Login attempt for user: {}", user.getEmail());

        // Логируем входящие данные
        logger.info("Received email: {}, password: {}", user.getEmail(), user.getPassword());

        User existingUser = userService.findByEmail(user.getEmail());

        // Проверка на null для existingUser
        if (existingUser == null) {
            logger.warn("User not found: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User not found"));
        }

        if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());
            logger.info("User {} successfully logged in", user.getEmail());
            return ResponseEntity.ok(new LoginResponse(token, existingUser.getRole()));
        }

        logger.warn("Invalid login attempt for user: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials"));
    }

    // Метод для отображения страницы аутентификации
    @GetMapping("/login")
    public String showLoginPage() {
        return "authenticationPage";
    }

    // Метод для отображения страницы регистрации
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registerNewUser";
    }

    // Класс для ответа на запрос входа
    public static class LoginResponse {
        private String token;
        private String role; // Добавляем поле для роли

        public LoginResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() {
            return token;
        }

        public String getRole() {
            return role;
        }
    }

    // Класс для ответа об ошибке
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