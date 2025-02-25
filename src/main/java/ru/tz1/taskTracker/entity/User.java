package ru.tz1.taskTracker.entity;

import jakarta.persistence.*;

/**
 * Класс, представляющий сущность пользователя в приложении Task Tracker.
 * Содержит информацию об идентификаторе, имени, email, пароле и роли пользователя.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // Уникальный идентификатор пользователя

    @Column(name = "email", nullable = false, unique = true) // Уникальный Email
    private String email;

    @Column(name = "name", nullable = false) // Имя пользователя
    private String name;

    @Column(name = "password", nullable = false) // Пароль
    private String password;

    @Column(name = "role", nullable = false) // Роль (ADMIN или USER)
    private String role;

    /**
     * Конструктор по умолчанию, требуемый для JPA.
     */
    public User() {}

    /**
     * Полный конструктор для создания нового пользователя.
     *
     * @param name     Имя пользователя.
     * @param email    Электронная почта пользователя.
     * @param password Пароль пользователя.
     * @param role     Роль пользователя (ADMIN или USER).
     */
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}