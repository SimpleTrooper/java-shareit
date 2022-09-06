package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.base.exceptions.DuplicateKeyException;

public class DuplicateEmailException extends DuplicateKeyException {
    public DuplicateEmailException(final String message) {
        super(message);
    }
}
