package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

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
                                .item(ItemMapper.toDto(booking.getItem()))
                                .start(booking.getStart())
                                .end(booking.getEnd())
                                .status(booking.getStatus())
                                .booker(UserMapper.toDto(booking.getBooker()))
                                .build();
    }
}
