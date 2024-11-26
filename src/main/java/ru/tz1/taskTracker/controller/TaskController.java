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
     * Проверяет валидность объекта задачи.
     * Выполняет проверки минимальной длины заголовка и описания задачи.
     *
     * @param task Объект задачи для проверки
     * @return true, если задача прошла валидацию; false в противном случае
     */
    private boolean isValidTask(Task task) {
        // Простые проверки на валидность
        if (task.getTitle() == null || task.getTitle().length() < 3) {
            System.out.println("Validation failed: Title is too short.");
            return false;
        }
        if (task.getDescription() == null || task.getDescription().length() < 10) {
            System.out.println("Validation failed: Description is too short.");
            return false;
        }
        // Добавьте другие проверки по необходимости
        return true;
    }

    /**
     * Создает новую задачу.
     * Логирует данные, полученные из формы, и проверяет их на валидность.
     * Если задача валидна, она будет сохранена; в противном случае будет возвращена форма.
     *
     * @param task Объект задачи, полученный из запроса
     * @return Строка для перенаправления на главную страницу или имя HTML-шаблона для создания задачи
     */
    @PostMapping("/tasks")
    public String createTask(@ModelAttribute Task task) {
        // Логируем полученные данные
        System.out.println("Received task for creation: " + task);

        // Валидация данных
        if (isValidTask(task)) {
            taskService.createTask(task);
            return "redirect:/mainPage"; // Перенаправляем на список задач после создания
        } else {
            // Здесь можно добавить логику для отображения сообщения об ошибке
            return "newTaskForm"; // Возвращаем на форму с ошибкой
        }
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