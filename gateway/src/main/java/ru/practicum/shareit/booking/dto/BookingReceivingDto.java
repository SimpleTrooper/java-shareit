package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.base.groups.OnCreate;
import ru.practicum.shareit.booking.validator.BookingTimeValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO бронирования для получения
 */
@Data
@Builder
@BookingTimeValid
public class BookingReceivingDto {
    @NotNull(groups = OnCreate.class)
    @Future
    private LocalDateTime start;
    @NotNull(groups = OnCreate.class)
    @Future
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}
