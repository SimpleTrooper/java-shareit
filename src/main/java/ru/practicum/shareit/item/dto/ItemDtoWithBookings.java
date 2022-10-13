package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class ItemDtoWithBookings extends ItemDto {
    private BookingShort lastBooking, nextBooking;

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class BookingShort {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime start;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
                               BookingShort nextBooking, Long requestId) {
        super(id, name, description, available, comments, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
