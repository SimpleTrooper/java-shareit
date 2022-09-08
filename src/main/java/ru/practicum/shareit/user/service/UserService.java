package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * Интерфейс бизнес-логики пользователей
 */
public interface UserService {
    UserDto getById(Long userId);

    List<UserDto> getAll();

    UserDto add(UserDto user);

    UserDto update(Long userId, UserDto user);

    boolean delete(Long userId);
}
