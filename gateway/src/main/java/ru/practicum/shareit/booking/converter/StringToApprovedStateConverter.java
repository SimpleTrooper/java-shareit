package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.dto.ApprovedState;
import ru.practicum.shareit.exception.IllegalRequestStateException;

/**
 * Конвертер строки запроса в состояния ApprovedState
 */
public class StringToApprovedStateConverter implements Converter<String, ApprovedState> {
    @Override
    public ApprovedState convert(String from) {
        try {
            return ApprovedState.valueOf(from.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalRequestStateException("Unknown state: " + from);
        }
    }
}
