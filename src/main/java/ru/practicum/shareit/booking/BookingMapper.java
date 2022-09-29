package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;

/**
 * Маппер для бронирований
 */
public class BookingMapper {
    public static Booking toBooking(BookingReceivingDto bookingDto) {
        return Booking.builder()
                      .id(bookingDto.getId())
                      .start(bookingDto.getStart())
                      .end(bookingDto.getEnd())
                      .status(bookingDto.getStatus())
                      .build();
    }

    public static BookingSendingDto toSendingDto(Booking booking) {
        return BookingSendingDto.builder()
                                .id(booking.getId())
                                .item(BookingSendingDto.BookingItem.toBookingItem(booking.getItem()))
                                .start(booking.getStart())
                                .end(booking.getEnd())
                                .status(booking.getStatus())
                                .booker(BookingSendingDto.BookingUser.toBookingUser(booking.getBooker()))
                                .build();
    }
}
