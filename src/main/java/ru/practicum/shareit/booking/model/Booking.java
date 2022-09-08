package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * Класс бронирования для работы с БД
 */
@Data
@Builder
public class Booking {
    private long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final long bookerId;
    private BookingStatus status;
}
