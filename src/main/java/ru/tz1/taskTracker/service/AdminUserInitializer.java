package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tz1.taskTracker.entity.User;

/**
 * Класс для инициализации администратора нашего сервиса с привилегиями администратора.
 * Выполняется при старте приложения для создания администратора, если он еще не существует.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService; // Сервис для управления пользователями

    @Autowired
    private PasswordEncoder passwordEncoder; // Кодировщик паролей

    @Value("${admin.email}")
    private String adminEmail; // Электронная почта администратора

    @Value("${admin.password}")
    private String adminPassword; // Пароль администратора

    @Value("${admin.name}") // Имя администратора
    private String adminName;

    /**
     * Метод, который будет выполнен при старте приложения.
     * Проверяет, существует ли пользователь с указанной электронной почтой.
     * Если пользователь не найден, создается новый администратор с
     * зашифрованным паролем.
     *
     * @param args Аргументы командной строки (не используются).
     * @throws Exception При возникновении ошибок выполнения.
     */
    @Override
    public void run(String... args) throws Exception {
        if (userService.findByEmail(adminEmail) == null) {
            createAdminUser(adminEmail, adminPassword, adminName, "ADMIN");
        }
    }

    /**
     * Создает нового пользователя администратора, если его нет.
     *
     * @param email Электронная почта нового администратора.
     * @param password Пароль нового администратура.
     * @param name Имя нового администратора.
     * @param role Роль нового администратора (должна быть "ADMIN").
     */
    private void createAdminUser(String email, String password, String name, String role) {
        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password)); // Хешируем пароль
        adminUser.setName(name); // Устанавливаем имя администратора
        adminUser.setRole(role); // Устанавливаем роль как строку

        userService.registerUser(adminUser); // Регистрируем пользователя
    }
}