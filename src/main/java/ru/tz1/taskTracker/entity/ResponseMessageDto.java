package ru.tz1.taskTracker.entity;

/**
 * Класс DTO (Data Transfer Object) для формирования ответа для фронтенда.
 * Используется для упаковки JSON-ответа от сервера.
 */
public class ResponseMessageDto {
    private String message;

    /**
     * Конструктор для создания объекта ResponseMessageDto с заданным сообщением.
     *
     * @param message Сообщение, которое будет отправлено на фронтенд.
     */
    public ResponseMessageDto(String message) {
        this.message = message;
    }

    /**
     * Получает сообщение.
     *
     * @return Сообщение, заключенное в объекте ResponseMessageDto.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает новое сообщение.
     *
     * @param message Новое сообщение для данного объекта.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}