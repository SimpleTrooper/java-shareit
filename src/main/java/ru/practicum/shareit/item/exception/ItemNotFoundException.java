package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.base.exceptions.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(final String message) {
        super(message);
    }
}
