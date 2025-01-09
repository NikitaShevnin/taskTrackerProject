package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword) {
        // Проверяем, существует ли пользователь с таким email
        if (userService.findByEmail(email) != null) {
            return "redirect:/api/auth/register?error=email_taken"; // Перенаправление с ошибкой
        }

        // Если пароли не совпадают
        if (!password.equals(confirmPassword)) {
            return "redirect:/api/auth/register?error=password_mismatch"; // Перенаправление с ошибкой
        }

        // Создаём нового пользователя
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole("USER"); // Установка роли по умолчанию

        // обрабатываем исключения
        try {
            userService.registerUser(newUser);
        } catch (Exception e) {
            // Логируем ошибку
            System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
            return "redirect:/api/auth/register?error=registration_failed"; // Перенаправление с ошибкой
        }

        return "redirect:/api/auth/loginPage"; // Перенаправление на страницу входа
    }
}