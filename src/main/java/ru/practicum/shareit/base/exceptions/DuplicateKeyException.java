package ru.practicum.shareit.base.exceptions;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(final String message) {
        super(message);
    }
}
