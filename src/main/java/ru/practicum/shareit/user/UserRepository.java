package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс-репозиторий пользователей
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
