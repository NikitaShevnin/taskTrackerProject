package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping("/delete-non-admins")
    public ResponseEntity<String> deleteNonAdminUsers() {
        userService.deleteNonAdminUsers();
        return ResponseEntity.ok("Все ненужные пользователи успешно удалены.");
    }

    @DeleteMapping("/{userId}") // Изменяем параметр на userId
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) { // Изменяем параметр на userId
        userService.deleteUserById(userId); // Изменяем вызов метода на userId
        return ResponseEntity.ok("Пользователь с id " + userId + " успешно удален."); // Изменяем вывод на userId
    }
}