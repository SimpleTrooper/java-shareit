package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingShort;

import java.util.List;

/**
 * DTO вещи с бронированиями
 */
@Getter
@Setter
public class ItemDtoWithBookings extends ItemDto {
    private BookingShort lastBooking, nextBooking;

    @Builder(builderMethodName = "builderWithBookings")
    public ItemDtoWithBookings(Long id, String name, String description, Boolean available,
                               List<CommentDto> commentsDto, BookingShort lastBooking,
                               BookingShort nextBooking) {
        super(id, name, description, available, commentsDto);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
