package ru.tz1.taskTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tz1.taskTracker.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsPasswordValid_CorrectPassword() {
        String rawPassword = "qwerty1";
        String encodedPassword = "$2a$10$nQncD4cUg23bMjobcK5npOBIcbikcC9wz02N6glFRPp.0e0.RAzRm"; // Пример хеша (сделайте его действительным хешем)

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean isValid = userService.isPasswordValid(rawPassword, encodedPassword);
        assertTrue(isValid);
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        System.out.println("Expected match result: " + true);
    }

    @Test
    public void testIsPasswordValid_IncorrectPassword() {
        String rawPassword = "testPass";
        String encodedPassword = "$2a$10$exampleHash";

        // Настройка мока
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Вызов метода
        boolean isValid = userService.isPasswordValid(rawPassword, encodedPassword);
        assertFalse(isValid);

        // Вывод результата для расшифровки
        System.out.println("Password match: " + isValid);
    }
    @Test
    public void testIsPasswordHashValid_ValidHash() {
        String validHash = "$2a$10$exampleHash";
        assertTrue(userService.isPasswordHashValid(validHash));
    }

    @Test
    public void testIsPasswordHashValid_InvalidHash() {
        String invalidHash = "invalidHash";
        assertFalse(userService.isPasswordHashValid(invalidHash));
    }
}