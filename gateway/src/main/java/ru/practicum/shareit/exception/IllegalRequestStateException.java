package ru.practicum.shareit.exception;

public class IllegalRequestStateException extends RuntimeException {
    public IllegalRequestStateException(final String message) {
        super(message);
    }
}
