package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.entity.UserRegistrationDto;
import ru.tz1.taskTracker.repository.UserRepository;

/**
 * Сервис для регистрации пользователей.
 * Обеспечивает функциональность для создания новых пользователей и управления процессом регистрации.
 */
@Service
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository; // Репозиторий для работы с пользователями

    @Autowired
    private PasswordEncoder passwordEncoder; // Кодировщик паролей для хеширования

    /**
     * Регистрирует нового пользователя.
     *
     * @param userDto Данные для регистрации пользователя, включая имя, email и пароли.
     * @return Сообщение об успешной регистрации.
     * @throws Exception Если возникла ошибка, например, если email уже занят или пароли не совпадают.
     */
    public String registerNewUser(UserRegistrationDto userDto) throws Exception {
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new Exception("Email уже занят"); // Выбрасываем исключение, если email уже занят
        }

        // Проверяем, совпадают ли введенные пароли
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new Exception("Пароли не совпадают"); // Выбрасываем исключение, если пароли не совпадают
        }

        // Создаем нового пользователя
        User newUser = new User();
        newUser.setName(userDto.getName()); // Устанавливаем имя пользователя
        newUser.setEmail(userDto.getEmail()); // Устанавливаем email пользователя
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword())); // Хешируем пароль перед сохранением
        newUser.setRole("USER"); // Устанавливаем роль по умолчанию "USER"

        // Сохраняем нового пользователя в базе данных
        userRepository.save(newUser);

        return "Регистрация прошла успешно"; // Возвращаем сообщение об успешной регистрации
    }
}