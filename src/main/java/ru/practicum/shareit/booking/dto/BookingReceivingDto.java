package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.base.validation.groups.OnCreate;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.validator.BookingTimeValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * DTO бронирования для получения
 */
@Data
@Builder
@BookingTimeValid
public class BookingReceivingDto {
    @Null(groups = OnCreate.class)
    private Long id;
    @NotNull(groups = OnCreate.class)
    @Future
    private LocalDateTime start;
    @NotNull(groups = OnCreate.class)
    @Future
    private LocalDateTime end;
    private Long itemId;
    @Null(groups = OnCreate.class)
    private BookingStatus status;
}
