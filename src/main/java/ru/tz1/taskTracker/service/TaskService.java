package ru.tz1.taskTracker.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tz1.taskTracker.entity.Task;
import ru.tz1.taskTracker.repository.TaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления задачами.
 * Предоставляет методы для создания, получения, обновления и удаления задач.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Создает новую задачу.
     *
     * @param task задача, которую нужно создать
     * @return сохраненная задача
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return Опциональная задача, если она найдена, иначе пустое значение
     */
    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Получает список всех задач.
     *
     * @return список задач
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи для обновления
     * @param taskDetails новые данные задачи
     * @return обновленная задача
     */
    public Task updateTask(Long id, Task taskDetails) {
        logger.info("Received request to update task with ID: {}", id);

        // Находим задачу по идентификатору, выбрасываем исключение, если задача не найдена
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Task not found with ID: {}", id);
                    return new EntityNotFoundException("Task not found with ID: " + id);
                });

        // Обновляем поля задачи
        logger.info("Updating task with ID: {}", id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());

        // Сохраняем обновленную задачу и возвращаем ее
        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: ID = {}, Updated task = {}", id, updatedTask);

        return updatedTask;
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id идентификатор задачи, которую нужно удалить
     */
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}