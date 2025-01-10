package ru.tz1.taskTracker.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.repository.UserRepository;

import java.util.List;

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

    public void printAllUsers() {
        List<User> users = userRepository.findAll(); // Получаем всех пользователей
        for (User user : users) {
            System.out.printf("id: %s, Email: %s, Password: %s, Role: %s%n",
                    user.getUserId(), user.getEmail(), user.getPassword(), user.getRole());
        }
    }

    @Transactional
    public void deleteUserById(Long userId) {
        // Проверяем, существует ли пользователь с таким id
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId); // Удаляем пользователя по id
        } else {
            throw new IllegalArgumentException("Пользователь с id " + userId + " не найден.");
        }
    }

    @Transactional // Обеспечивает выполнение этого метода в рамках одной транзакции
    public void deleteNonAdminUsers() {
        // Находим всех пользователей, которые не являются администраторами
        List<User> nonAdminUsers = userRepository.findByRoleNot("ADMIN");
        userRepository.deleteAll(nonAdminUsers); // Удаляем найденных пользователей
    }
}