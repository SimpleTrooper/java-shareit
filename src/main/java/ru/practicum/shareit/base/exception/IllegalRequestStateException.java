package ru.practicum.shareit.base.exception;

public class IllegalRequestStateException extends RuntimeException {
    public IllegalRequestStateException(final String message) {
        super(message);
    }
}
