package ru.practicum.shareit.request;

import ru.practicum.shareit.base.exception.NotFoundException;

public class ItemRequestNotFoundException extends NotFoundException {
    public ItemRequestNotFoundException(final String message) {
        super(message);
    }
}
