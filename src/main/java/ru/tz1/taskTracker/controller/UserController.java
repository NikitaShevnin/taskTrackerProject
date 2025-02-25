package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.service.UserService;

/**
 * Контроллер для управления пользователями в приложении Task Tracker.
 * Обрабатывает запросы, связанные с удалением пользователей.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Метод для удаления всех пользователей, которые не являются администраторами.
     *
     * @return ResponseEntity с сообщением об успешном удалении ненужных пользователей.
     */
    @DeleteMapping("/delete-non-admins")
    public ResponseEntity<String> deleteNonAdminUsers() {
        userService.deleteNonAdminUsers();
        return ResponseEntity.ok("Все ненужные пользователи успешно удалены.");
    }

    /**
     * Метод для удаления пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя, которого необходимо удалить.
     * @return ResponseEntity с сообщением об успешном удалении пользователя.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("Пользователь с id " + userId + " успешно удален.");
    }
}