package ru.tz1.taskTracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий объект рабочей задачи в приложении Task Tracker.
 * Содержит информацию о заголовке, описании, статусе, приоритете и других атрибутах задачи.
 */
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String title;
    private String description;

    @Column(nullable = false)
    private String status; // "в ожидании", "в процессе", "завершено"

    @Column(nullable = false)
    private String priority; // "высокий", "средний", "низкий"

    private LocalDate createdDate; // Дата создания
    private LocalDate deadline; // Дата дедлайна

    @ElementCollection
    @CollectionTable(name = "task_comments", joinColumns = @JoinColumn(name = "task_id"))
    private List<String> comments = new ArrayList<>(); // Список комментариев

    /**
     * Конструктор без параметров для создания экземпляра задачи.
     */
    public Task() {
    }

    /**
     * Конструктор для создания экземпляра задачи с заданными параметрами.
     *
     * @param taskId      Идентификатор задачи.
     * @param title       Заголовок задачи.
     * @param description Описание задачи.
     * @param status      Статус задачи.
     * @param priority    Приоритет задачи.
     * @param createdDate Дата создания задачи.
     * @param deadline    Дата дедлайна задачи.
     * @param comments    Список комментариев к задаче.
     */
    public Task(Long taskId, String title, String description, String status,
                String priority, LocalDate createdDate, LocalDate deadline,
                List<String> comments) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdDate = createdDate;
        this.deadline = deadline;
        this.comments = comments;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}