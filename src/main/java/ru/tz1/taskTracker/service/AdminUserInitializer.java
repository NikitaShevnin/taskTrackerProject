package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Метод, который будет выполнен при старте приложения.
     * Проверяет, существует ли пользователь с адресом электронной почты
     * "admin@example.com". Если пользователь не найден, создается новый
     * администратор с предустановленным электронным адресом и
     * зашифрованным паролем. Если администратор уже существует,
     * никаких действий не предпринимается.
     */
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";
        if (userService.findByEmail(adminEmail) == null) {
            createAdminUser(adminEmail, "adminPassword123", "ADMIN");
        }
    }

    /**
     * Создадим нового пользователя администратора если его нет
     */
    private void createAdminUser(String email, String password, String role) {
        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setRole(String.valueOf(Collections.singletonList(role)));

        userService.registerUser(adminUser);
    }
}
