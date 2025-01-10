package ru.tz1.taskTracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;
    private String password;
    private String role; // "ADMIN" или "USER"

    // Конструктор по умолчанию (требуется для JPA)
    public User() {}

    // Полный конструктор
    public User(Long id, String email, String password, String role) {
        this.userId = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Геттеры и сеттеры

    public Long getUserid() {
        return userId;
    }

    public void setUserid(Long userid) {
        this.userId = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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