package ru.tz1.taskTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tz1.taskTracker.entity.Task;
import ru.tz1.taskTracker.service.TaskService;

import java.util.List;

/**
 * Контроллер для управления задачами в приложении Task Tracker.
 */
@Controller
@RequestMapping("/tasks")
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
     * Отображает все задачи.
     *
     * @param model Модель для передачи данных в шаблон
     * @return Имя HTML-шаблона для отображения списка задач
     */
    @GetMapping
    public String getAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "getAllTasks"; // Имя HTML-шаблона для списка задач
    }

    /**
     * Создает новую задачу.
     *
     * @param task Объект задачи, полученный из запроса
     * @return Созданная задача
     */
    @PostMapping
    @ResponseBody
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    /**
     * Удаляет задачу по заданному идентификатору.
     *
     * @param id Идентификатор задачи, которую нужно удалить
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}