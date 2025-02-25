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

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class); // Инициализируем логгер

    @Autowired
    private TaskRepository taskRepository; // Репозиторий для работы с задачами

    /**
     * Создает новую задачу.
     *
     * @param task задача, которую нужно создать
     * @return сохраненная задача
     */
    public Task createTask(Task task) {
        return taskRepository.save(task); // Сохраняем новую задачу и возвращаем ее
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return задача, если она найдена, иначе null
     */
    public Task getTaskById(Long taskId) {
        logger.info("Received request to get task with ID: {}", taskId); // Логируем получение запроса
        // Ищем задачу по идентификатору
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            logger.info("Task found: {}", optionalTask.get()); // Логируем, если задача найдена
            return optionalTask.get(); // Возвращаем найденную задачу
        } else {
            logger.warn("Task not found with ID: {}", taskId); // Логируем, если задача не найдена
            return null; // Возвращаем null, если задача не найдена
        }
    }

    /**
     * Получает список всех задач.
     *
     * @return список задач
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll(); // Возвращаем список всех задач
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param taskId идентификатор задачи для обновления
     * @param taskDetails новые данные задачи
     * @return обновленная задача
     */
    public Task updateTask(Long taskId, Task taskDetails) {
        logger.info("Received request to update task with ID: {}", taskId); // Логируем запрос на обновление

        // Находим задачу по идентификатору, выбрасываем исключение, если задача не найдена
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task not found with ID: {}", taskId); // Логируем ошибку, если задача не найдена
                    return new EntityNotFoundException("Task not found with ID: " + taskId); // Выбрасываем исключение
                });

        // Обновляем поля задачи с новыми значениями
        logger.info("Updating task with ID: {}", taskId);
        task.setTitle(taskDetails.getTitle()); // Обновляем название задачи
        task.setDescription(taskDetails.getDescription()); // Обновляем описание задачи
        task.setStatus(taskDetails.getStatus()); // Обновляем статус задачи
        task.setPriority(taskDetails.getPriority()); // Обновляем приоритет задачи
        task.setCreatedDate(taskDetails.getCreatedDate()); // Обновление даты создания
        task.setDeadline(taskDetails.getDeadline()); // Обновление даты дедлайна
        task.setComments(taskDetails.getComments()); // Обновление комментариев

        // Сохраняем обновленную задачу в репозитории и возвращаем ее
        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: ID = {}, Updated task = {}", taskId, updatedTask); // Логируем успешное обновление

        return updatedTask;
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param taskId идентификатор задачи, которую нужно удалить
     */
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId); // Удаляем задачу по идентификатору
    }
}