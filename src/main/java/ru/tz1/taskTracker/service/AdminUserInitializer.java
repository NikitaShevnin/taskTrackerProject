package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tz1.taskTracker.entity.User;

import java.util.Collections;

/**
 * Класс для инициализации администратора нашего сервиса с привилегиями администратора.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    /**
     * Метод, который будет выполнен при старте приложения.
     * Проверяет, существует ли пользователь с указанной электронной почтой.
     * Если пользователь не найден, создается новый администратор с
     * зашифрованным паролем.
     */
    @Override
    public void run(String... args) throws Exception {
        if (userService.findByEmail(adminEmail) == null) {
            createAdminUser(adminEmail, adminPassword, "ADMIN");
        }
    }

    /**
     * Создадим нового пользователя администратора, если его нет
     */
    private void createAdminUser(String email, String password, String role) {
        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setRole(String.valueOf(Collections.singletonList(role)));

        userService.registerUser(adminUser);
    }
}