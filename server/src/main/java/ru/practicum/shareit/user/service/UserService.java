package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

/**
 * Интерфейс бизнес-логики пользователей
 */
public interface UserService {
    UserDto findById(Long userId);

    List<UserDto> findAll();

    UserDto add(UserDto user);

    UserDto update(Long userId, UserDto user);

    void delete(Long userId);
}
