package ru.tz1.taskTracker.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Обеспечивает функциональность для регистрации, удаления и проверки пользователей.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Репозиторий для работы с пользователями

    @Autowired
    private PasswordEncoder passwordEncoder; // Интерфейс для хеширования паролей

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // Логгер для отслеживания событий

    /**
     * Метод для регистрации нового пользователя.
     * Перед сохранением пароль хэшируется.
     *
     * @param user пользователь для регистрации
     * @return зарегистрированный пользователь
     */
    public User registerUser(User user) {
        user.setPassword(encodePassword(user.getPassword())); // Хэшируем пароль
        return userRepository.save(user); // Сохраняем пользователя в репозитории
    }

    /**
     * Метод для поиска пользователя по email.
     *
     * @param email email пользователя
     * @return пользователь с указанным email
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
            System.out.printf("id: %s, Name: %s, Email: %s, Password: %s, Role: %s%n",
                    user.getUserId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole());
        }
    }

    /**
     * Метод для удаления пользователя по его ID.
     *
     * @param userId идентификатор пользователя
     * @throws IllegalArgumentException если пользователь с таким ID не найден
     */
    @Transactional
    public void deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId); // Удаляем пользователя по ID
        } else {
            throw new IllegalArgumentException("Пользователь с id " + userId + " не найден."); // Выбрасываем исключение, если пользователь не найден
        }
    }

    /**
     * Метод для удаления всех пользователей, кроме администраторов.
     */
    @Transactional
    public void deleteNonAdminUsers() {
        List<User> nonAdminUsers = userRepository.findByRoleNot("ADMIN"); // Находим всех пользователей, кроме администраторов
        userRepository.deleteAll(nonAdminUsers); // Удаляем найденных пользователей
    }

    /**
     * Метод для полного очищения базы данных в случае если изменится
     * структура БД и надо будет создать всё заново.
     */
    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll(); // Удаляем всех пользователей
    }

    /**
     * Метод для хэширования пароля.
     *
     * @param rawPassword сырой (нехэшированный) пароль
     * @return хэшированный пароль
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // Хешируем пароль и возвращаем
    }

    /**
     * Проверка валидности пароля.
     *
     * @param rawPassword    сырой (нехэшированный) пароль
     * @param encodedPassword хэшированный пароль
     * @return true, если пароль валиден, иначе false
     */
    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        logger.debug("Checking password validity..."); // Логируем начало проверки
        logger.debug("Raw Password: {}", rawPassword); // Логируем сырой пароль
        logger.debug("Encoded Password: {}", encodedPassword); // Логируем закодированный пароль

        if (!isPasswordHashValid(encodedPassword)) {
            return false; // Невалидный формат хэша
        }

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword); // Проверяем совпадение паролей
        logger.debug("Password matches: {}", matches); // Логируем результат
        return matches; // Возвращаем результат
    }

    /**
     * Метод для проверки, является ли строка допустимым хэшем пароля.
     *
     * @param passwordHash строка, которую нужно проверить
     * @return true, если строка является допустимым хэшем пароля, иначе false
     */
    public boolean isPasswordHashValid(String passwordHash) {
        return passwordHash != null &&
                (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$") || passwordHash.startsWith("$2y$")); // Проверяем, является ли хэш корректным
    }
}