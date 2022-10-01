package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.base.exception.IllegalRequestStateException;
import ru.practicum.shareit.booking.model.BookingRequestState;

/**
 * Конвертер строки запроса в состояния BookingRequestState
 */
public class StringToBookingRequestStateConverter implements Converter<String, BookingRequestState> {
    @Override
    public BookingRequestState convert(String from) {
        try {
            return BookingRequestState.valueOf(from.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalRequestStateException("Unknown state: " + from);
        }
    }
}
