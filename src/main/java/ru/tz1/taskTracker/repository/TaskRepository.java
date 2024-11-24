package ru.tz1.taskTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tz1.taskTracker.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
