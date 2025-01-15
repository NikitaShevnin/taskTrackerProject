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

    /**
     * Метод для регистрации нового пользователя.
     * Перед сохранением пароль хэшируется.
     *
     * @param user пользователь для регистрации
     * @return зарегистрированный пользователь
     */
    public User registerUser(User user) {
        user.setPassword(encodePassword(user.getPassword())); // Хэшируем пароль
        return userRepository.save(user); // Сохраняем пользователя
    }

    /**
     * Метод для поиска пользователя по email.
     *
     * @param email email пользователя
     * @return пользователь
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email); // Находим пользователя по email
    }

    /**
     * Метод для вывода всех пользователей в консоль.
     */
    public void printAllUsers() {
        List<User> users = userRepository.findAll(); // Получаем всех пользователей
        for (User user : users) {
            System.out.printf("id: %s, Email: %s, Password: %s, Role: %s%n",
                    user.getUserId(), user.getEmail(), user.getPassword(), user.getRole());
        }
    }

    /**
     * Метод для удаления пользователя по его ID.
     *
     * @param userId идентификатор пользователя
     */
    @Transactional
    public void deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId); // Удаляем пользователя по id
        } else {
            throw new IllegalArgumentException("Пользователь с id " + userId + " не найден.");
        }
    }

    /**
     * Метод для удаления всех пользователей, кроме администраторов.
     */
    @Transactional
    public void deleteNonAdminUsers() {
        List<User> nonAdminUsers = userRepository.findByRoleNot("ADMIN");
        userRepository.deleteAll(nonAdminUsers); // Удаляем найденных пользователей
    }

    /**
     * Метод для хэширования пароля.
     *
     * @param rawPassword сырой (нехэшированный) пароль
     * @return хэшированный пароль
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Проверка валидности пароля.
     *
     * @param rawPassword    сырой (нехэшированный) пароль
     * @param encodedPassword хэшированный пароль
     * @return true, если пароль валиден, иначе false
     */
    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        if (!isPasswordHashValid(encodedPassword)) {
            return false; // Невалидный формат хэша
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Метод для проверки, является ли строка допустимым хэшем пароля.
     *
     * @param passwordHash строка, которую нужно проверить
     * @return true, если строка является допустимым хэшем пароля, иначе false
     */
    public boolean isPasswordHashValid(String passwordHash) {
        return passwordHash != null &&
                (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$") || passwordHash.startsWith("$2y$"));
    }
}
