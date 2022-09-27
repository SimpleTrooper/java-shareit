package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.base.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(final String message) {
        super(message);
    }
}
