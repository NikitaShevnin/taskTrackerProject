package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.ResponseMessageDto;
import ru.tz1.taskTracker.entity.UserRegistrationDto;
import ru.tz1.taskTracker.service.UserRegistrationService;

@RestController
@RequestMapping("/api/auth")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService userRegService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessageDto> registerUser(@RequestBody UserRegistrationDto userDto) {
        try {
            // Вызов метода сервиса для регистрации нового пользователя
            String registrationResponse = userRegService.registerNewUser(userDto);
            return ResponseEntity.ok(new ResponseMessageDto(registrationResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessageDto(e.getMessage()));
        }
    }
}
