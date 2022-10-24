package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.base.exception.ResourceAccessException;

public class InvalidItemOwnerException extends ResourceAccessException {
    public InvalidItemOwnerException(final String message) {
        super(message);
    }
}
