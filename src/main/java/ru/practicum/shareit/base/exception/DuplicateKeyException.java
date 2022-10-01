package ru.practicum.shareit.base.exception;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(final String message) {
        super(message);
    }
}
