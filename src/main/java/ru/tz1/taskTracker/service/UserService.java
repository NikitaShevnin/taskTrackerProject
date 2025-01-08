package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Используем интерфейс

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Хешируем пароль
        return userRepository.save(user); // Сохраняем пользователя
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email); // Находим пользователя по email
    }
}