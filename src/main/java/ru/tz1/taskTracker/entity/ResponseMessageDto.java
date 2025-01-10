package ru.tz1.taskTracker.entity;

/**
 * Класс DTO для формирования ответа для фронтенда.
 * Нужен для упаковки JSON ответа от сервера.
 */
public class ResponseMessageDto {
    private String message;

    public ResponseMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
