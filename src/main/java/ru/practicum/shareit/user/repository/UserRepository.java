package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Интерфейс-репозиторий пользователей
 */
public interface UserRepository {
    User getById(Long userId);

    List<User> getAll();

    User add(User user);

    User update(User user);

    boolean delete(Long userId);
}
