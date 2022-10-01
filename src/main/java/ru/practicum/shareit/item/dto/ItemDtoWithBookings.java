package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO вещи с бронированиями
 */
@Getter
@Setter
public class ItemDtoWithBookings extends ItemDto {
    private BookingShort lastBooking, nextBooking;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class BookingShort {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;

        public static BookingShort toBookingShort(Booking booking) {
            return BookingShort.builder()
                               .id(booking.getId())
                               .start(booking.getStart())
                               .end(booking.getEnd())
                               .bookerId(booking.getBooker().getId())
                               .build();
        }
    }

    @Builder(builderMethodName = "builderWithBookings")
    public ItemDtoWithBookings(Long id, String name, String description, Boolean available,
                               List<ItemDto.ItemComment> comments, BookingShort lastBooking,
                               BookingShort nextBooking) {
        super(id, name, description, available, comments);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
