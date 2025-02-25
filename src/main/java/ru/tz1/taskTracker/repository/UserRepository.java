package ru.tz1.taskTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tz1.taskTracker.entity.User;

import java.util.List;

/**
 * Репозиторий для управления пользователями в приложении Task Tracker.
 * Обеспечивает операции доступа к данным для сущности User,
 * включая поиск пользователей по email и роли.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его электронному адресу.
     *
     * @param email Электронная почта пользователя.
     * @return Найденный пользователь или null, если пользователь не найден.
     */
    User findByEmail(String email);

    /**
     * Находит всех пользователей, которые не имеют указанную роль.
     *
     * @param role Роль, которую необходимо исключить из списка.
     * @return Список пользователей, не имеющих указанной роли.
     */
    List<User> findByRoleNot(String role);
}