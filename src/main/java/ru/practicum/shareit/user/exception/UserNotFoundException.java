package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.base.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(final String message) {
        super(message);
    }
}
