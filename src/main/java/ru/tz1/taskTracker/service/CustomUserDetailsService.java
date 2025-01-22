package ru.tz1.taskTracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.User; // Обязательно импортируйте вашу модель
import ru.tz1.taskTracker.repository.UserRepository; // Импортируйте ваш репозиторий

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Репозиторий для пользователей

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole()) // При условии, что это строка (не массив ролей)
                .build();
    }
}