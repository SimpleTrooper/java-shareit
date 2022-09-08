package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * DTO бронирования
 */
@Data
@Builder
public class BookingDto {
    private long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private BookingStatus status;
}
