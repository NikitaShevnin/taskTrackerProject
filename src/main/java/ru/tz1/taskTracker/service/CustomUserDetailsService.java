package ru.tz1.taskTracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.repository.UserRepository;

/**
 * Класс, реализующий интерфейс UserDetailsService для загрузки информации о пользователе.
 * Используется в процессе аутентификации для получения данных о пользователе по его электронной почте.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Репозиторий для работы с пользователями
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class); // Инициализируем логгер

    /**
     * Конструктор класса CustomUserDetailsService.
     *
     * @param userRepository Репозиторий для работы с пользователями.
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает информацию о пользователе по его электронной почте.
     *
     * @param email Электронная почта пользователя, чьи данные необходимо загрузить.
     * @return Объект UserDetails, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException Исключение, которое выбрасывается, если пользователь не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email); // Ищем пользователя по электронной почте
        if (user == null) {
            logger.error("User not found for email: {}", email); // Логируем ошибку, если пользователь не найден
            throw new UsernameNotFoundException("User not found"); // Выбрасываем исключение
        }
        // Возвращаем объект UserDetails, созданный на основе найденного пользователя
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}