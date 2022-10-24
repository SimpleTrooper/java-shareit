package ru.practicum.shareit.item.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(final String message) {
        super(message);
    }
}
