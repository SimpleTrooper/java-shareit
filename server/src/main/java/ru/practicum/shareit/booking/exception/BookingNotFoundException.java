package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.base.exception.NotFoundException;

public class BookingNotFoundException extends NotFoundException {
    public BookingNotFoundException(final String message) {
        super(message);
    }
}
