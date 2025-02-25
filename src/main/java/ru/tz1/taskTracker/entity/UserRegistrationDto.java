package ru.tz1.taskTracker.entity;

/**
 * Класс DTO (Data Transfer Object) для регистрации пользователей.
 * Используется для передачи данных, необходимых для регистрации нового пользователя.
 */
public class UserRegistrationDto {
    private String name; // Имя пользователя
    private String email; // Электронная почта пользователя
    private String password; // Пароль пользователя
    private String confirmPassword; // Подтверждение пароля

    /**
     * Получает имя пользователя.
     *
     * @return Имя пользователя.
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name Имя пользователя.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получает электронную почту пользователя.
     *
     * @return Электронная почта пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает электронную почту пользователя.
     *
     * @param email Электронная почта пользователя.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Получает пароль пользователя.
     *
     * @return Пароль пользователя.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password Пароль пользователя.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получает подтверждение пароля пользователя.
     *
     * @return Подтверждение пароля.
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Устанавливает подтверждение пароля пользователя.
     *
     * @param confirmPassword Подтверждение пароля.
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}