package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.base.exceptions.ResourceAccessException;

public class InvalidItemOwnerException extends ResourceAccessException {
    public InvalidItemOwnerException(final String message) {
        super(message);
    }
}
