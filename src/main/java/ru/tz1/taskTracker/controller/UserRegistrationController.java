package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.ResponseMessageDto;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.entity.UserRegistrationDto;
import ru.tz1.taskTracker.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> registerUser(@RequestBody UserRegistrationDto userDto) {
        // Проверяем, существует ли пользователь с таким email
        if (userService.findByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body(new ResponseMessageDto("Email уже занят"));
        }

        // Если пароли не совпадают
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ResponseMessageDto("Пароли не совпадают"));
        }

        // Создаем нового пользователя
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword())); // Хешируем пароль перед сохранением
        newUser.setRole("USER"); // Установка роли по умолчанию "USER"
        try {
            userService.registerUser(newUser);
        } catch (Exception e) {
            // Логируем ошибку
            System.err.println("Ошибка при регистрации пользователя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessageDto("Ошибка регистрации"));
        }
        return ResponseEntity.ok(new ResponseMessageDto("Регистрация прошла успешно"));
    }
}