package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Класс пользователя для работы с БД
 */
@Data
@AllArgsConstructor
@Builder
public class User {
    private long id;
    private String name;
    private String email;

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }
}
