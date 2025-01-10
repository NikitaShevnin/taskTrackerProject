package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.Task;
import ru.tz1.taskTracker.service.TaskService;
import ru.tz1.taskTracker.util.JwtUtil;

import java.util.List;

/**
 * Контроллер для управления задачами в приложении Task Tracker.
 * Обрабатывает запросы, связанные с созданием, отображением и удалением задач.
 */
@Controller
@RequestMapping("/")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/new")
    public String showNewTaskForm() {
        return "newTaskForm"; // Имя HTML-шаблона для создания новой задачи
    }

    @GetMapping("/mainPage")
    public String mainPage(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "mainPage"; // Имя HTML-шаблона для главной страницы
    }

    @GetMapping("/tasks")
    @ResponseBody
    public List<Task> getTasks() {
        return taskService.getAllTasks(); // Возвращаем все задачи
    }

    private boolean isValidTask(Task task) {
        if (task.getTitle() == null || task.getTitle().length() < 3) {
            System.out.println("Validation failed: Title is too short. Title: '" + task.getTitle() + "'");
            return false;
        }
        if (task.getDescription() == null || task.getDescription().length() < 10) {
            System.out.println("Validation failed: Description is too short. Description: '" + task.getDescription() + "'");
            return false;
        }
        System.out.println("Task is valid: " + task);
        return true;
    }

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtUtil.validateToken(jwtToken, jwtUtil.extractUsername(jwtToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid JWT token");
        }

        System.out.println("Received task for creation: " + task);

        boolean isValid = isValidTask(task);
        if (!isValid) {
            System.out.println("Validation failed for task: " + task);
            return ResponseEntity.badRequest().body("Invalid task data"); // Возвращаем сообщение об ошибке
        }

        try {
            taskService.createTask(task);
            System.out.println("Task created successfully: " + task);
            return ResponseEntity.ok().body(task); // Возвращаем созданную задачу
        } catch (Exception e) {
            System.out.println("Error while creating task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating task");
        }
    }

    @DeleteMapping("/tasks/{taskId}") // Изменяем параметр на taskId
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) { // Изменяем параметр на taskId
        try {
            taskService.deleteTask(taskId); // Удаляем задачу по taskId
            return ResponseEntity.ok().body("Task deleted successfully");
        } catch (Exception e) {
            System.out.println("Error while deleting task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting task");
        }
    }

    @PutMapping("/tasks/{taskId}") // Изменяем параметр на taskId
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody Task task) { // Изменяем параметр на taskId
        System.out.println("Received request to update task with ID: " + taskId);
        System.out.println("Received task data for update: " + task);

        boolean isValid = isValidTask(task);
        if (!isValid) {
            System.out.println("Validation failed for task: " + task);
            return ResponseEntity.badRequest().body("Invalid task data");
        }

        try {
            taskService.updateTask(taskId, task); // Обновляем задачу по taskId
            System.out.println("Task updated successfully: ID = " + taskId + ", Updated task = " + task);
            return ResponseEntity.ok().body(task); // Возвращаем обновленную задачу
        } catch (Exception e) {
            System.out.println("Error while updating task with ID " + taskId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating task");
        }
    }

    @GetMapping("/tasks/{taskId}") // Изменяем параметр на taskId
    @ResponseBody
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) { // Изменяем параметр на taskId
        Task task = taskService.getTaskById(taskId); // Используем taskId для получения задачи
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}