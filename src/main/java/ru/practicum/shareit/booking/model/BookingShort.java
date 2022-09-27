package ru.practicum.shareit.booking.model;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Проекция на Booking
 */
public interface BookingShort {
    Long getId();

    LocalDateTime getStart();

    LocalDateTime getEnd();

    @Value("#{target.booker.id}")
    Long getBookerId();
}
