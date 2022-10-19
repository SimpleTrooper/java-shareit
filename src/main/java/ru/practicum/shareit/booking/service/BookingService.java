package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;

import java.util.List;

/**
 * Интерфейс бизнес-логики бронирования
 */
public interface BookingService {
    BookingSendingDto add(Long bookerId, BookingReceivingDto bookingDto);

    BookingSendingDto handleStatus(Long userId, Long bookingId, ApprovedState approved);

    BookingSendingDto findById(Long userId, Long bookingId);

    List<BookingSendingDto> findByBookerIdAndStatus(Long bookerId, BookingRequestState state,
                                                    PaginationRequest paginationRequest);

    List<BookingSendingDto> findByOwnerIdAndStatus(Long bookerId, BookingRequestState state,
                                                   PaginationRequest paginationRequest);
}
