package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                      .id(bookingDto.getId())
                      .item(bookingDto.getItem())
                      .start(bookingDto.getStart())
                      .end(bookingDto.getEnd())
                      .status(bookingDto.getStatus())
                      .build();
    }

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                         .id(booking.getId())
                         .item(booking.getItem())
                         .start(booking.getStart())
                         .end(booking.getEnd())
                         .status(booking.getStatus())
                         .build();
    }
}
