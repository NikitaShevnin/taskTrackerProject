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
     * Отображает главную страницу.
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
     * Создает новую задачу.
     *
     * @param task Объект задачи, полученный из запроса
     * @return Перенаправление на страницу со списком задач
     */
    @PostMapping("/tasks")
    public String createTask(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/mainPage"; // Перенаправляем на список задач после создания
    }

    /**
     * Удаляет задачу по заданному идентификатору.
     *
     * @param id Идентификатор задачи, которую нужно удалить
     */
    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}