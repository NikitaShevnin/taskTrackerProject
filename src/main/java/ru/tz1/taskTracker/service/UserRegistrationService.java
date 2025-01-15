package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.entity.UserRegistrationDto;
import ru.tz1.taskTracker.repository.UserRepository;

@Service
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registerNewUser(UserRegistrationDto userDto) throws Exception {
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new Exception("Email уже занят");
        }

        // Если пароли не совпадают
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new Exception("Пароли не совпадают");
        }

        // Создаем нового пользователя
        User newUser = new User();
        newUser.setName(userDto.getName()); // Устанавливаем имя пользователя
        newUser.setEmail(userDto.getEmail()); // Устанавливаем email пользователя
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword())); // Хешируем пароль перед сохранением
        newUser.setRole("USER"); // Установка роли по умолчанию "USER"

        // Сохраняем нового пользователя в базе данных
        userRepository.save(newUser);

        return "Регистрация прошла успешно";
    }
}
