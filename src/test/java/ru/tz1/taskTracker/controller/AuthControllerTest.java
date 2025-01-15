package ru.tz1.taskTracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tz1.taskTracker.entity.User;
import ru.tz1.taskTracker.service.UserService;
import ru.tz1.taskTracker.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User existingUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("$2a$10$exampleHash"); // Заметьте, используйте действительный хеш пароля
        existingUser.setRole("USER");
    }

    @Test
    public void testLoginUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("myPassword"); // Вводимый пароль

        when(userService.findByEmail(user.getEmail())).thenReturn(existingUser);
        when(userService.isPasswordValid(user.getPassword(), existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(existingUser.getEmail())).thenReturn("mockJwtToken");

        ResponseEntity<?> response = authController.loginUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mockJwtToken", ((AuthController.LoginResponse) response.getBody()).getToken());
    }

    @Test
    public void testLoginUser_UserNotFound() {
        User user = new User();
        user.setEmail("nonexistent@example.com");
        user.setPassword("myPassword");

        when(userService.findByEmail(user.getEmail())).thenReturn(null); // Пользователь не найден

        ResponseEntity<?> response = authController.loginUser(user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", ((AuthController.ErrorResponse) response.getBody()).getMessage());
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("wrongPassword"); // Неверный пароль

        when(userService.findByEmail(user.getEmail())).thenReturn(existingUser);
        when(userService.isPasswordValid(user.getPassword(), existingUser.getPassword())).thenReturn(false);

        ResponseEntity<?> response = authController.loginUser(user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", ((AuthController.ErrorResponse) response.getBody()).getMessage());
    }
}