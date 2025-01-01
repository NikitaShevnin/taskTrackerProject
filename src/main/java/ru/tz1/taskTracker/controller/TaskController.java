package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.Task;
import ru.tz1.taskTracker.service.TaskService;

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

    /**
     * Отображает страницу для создания новой задачи.
     *
     * @return Имя HTML-шаблона для создания новой задачи
     */
    @GetMapping("/new")
    public String showNewTaskForm() {
        return "newTaskForm"; // Имя HTML-шаблона для создания новой задачи
    }

    /**
     * Отображает главную страницу со списком задач.
     *
     * @param model Модель для передачи данных в шаблон
     * @return Имя HTML-шаблона для главной страницы
     */
    @GetMapping("/mainPage")
    public String mainPage(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "mainPage"; // Имя HTML-шаблона для главной страницы
    }

    /**
     * Получает список всех задач.
     *
     * @return Список задач в формате JSON
     */
    @GetMapping("/tasks")
    @ResponseBody
    public List<Task> getTasks() {
        return taskService.getAllTasks(); // Возвращаем все задачи
    }

    /**
     * Проверяет валидность объекта задачи.
     * Выполняет проверки минимальной длины заголовка и описания задачи.
     *
     * @param task Объект задачи для проверки
     * @return true, если задача прошла валидацию; false в противном случае
     */
    private boolean isValidTask(Task task) {
        // Простые проверки на валидность
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

    /**
     * Создает новую задачу.
     * Логирует данные, полученные из формы, и проверяет их на валидность.
     * Если задача валидна, она будет сохранена; в противном случае возвращается сообщение об ошибке.
     *
     * @param task Объект задачи, полученный из запроса
     * @return ResponseEntity с данными задачи или сообщение об ошибке
     */
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        // Логируем полученные данные
        System.out.println("Received task for creation: " + task);

        // Валидация данных
        boolean isValid = isValidTask(task);
        if (!isValid) {
            // Логируем, что задача не валидна
            System.out.println("Validation failed for task: " + task);
            return ResponseEntity.badRequest().body("Invalid task data"); // Возвращаем сообщение об ошибке
        }

        // Сохранение задачи
        try {
            taskService.createTask(task);
            System.out.println("Task created successfully: " + task);
            return ResponseEntity.ok().body(task); // Возвращаем созданную задачу
        } catch (Exception e) {
            System.out.println("Error while creating task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating task"); // Возвращаем сообщение об ошибке
        }
    }

    /**
     * Удаляет задачу по заданному идентификатору.
     *
     * @param id Идентификатор задачи, которую нужно удалить
     * @return ResponseEntity с информацией о результате операции
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id); // Удаляем задачу
            return ResponseEntity.ok().body("Task deleted successfully"); // Возвращаем успешный ответ
        } catch (Exception e) {
            System.out.println("Error while deleting task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting task"); // Возвращаем сообщение об ошибке
        }
    }
}