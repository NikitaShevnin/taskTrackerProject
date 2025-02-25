package ru.tz1.taskTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tz1.taskTracker.entity.Task;

/**
 * Репозиторий для управления задачами в приложении Task Tracker.
 * Обеспечивает операции доступа к данным для сущности Task,
 * включая CRUD операции.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // JpaRepository предоставляет все необходимые методы для работы с сущностью Task.
}