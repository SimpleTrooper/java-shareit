package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.base.validation.groups.OnCreate;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер для бронирований
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Добавление бронирования
     *
     * @param userId     id пользователя
     * @param bookingDto DTO бронирования
     * @return DTO добавленной сущности
     */
    @PostMapping
    @Validated(OnCreate.class)
    public BookingSendingDto add(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingReceivingDto bookingDto) {
        log.info("Request to add new booking: {} from user: {}", bookingDto, userId);
        BookingSendingDto returnedBooking = bookingService.add(userId, bookingDto);
        if (returnedBooking != null) {
            log.info("Successfully added new booking: {}", returnedBooking);
        }
        return returnedBooking;
    }

    /**
     * Подтверждение/отказ брониования
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     * @param approved  подтверждение или отказ (true, false)
     * @return DTO обновленной сущности
     */
    @PatchMapping("/{bookingId}")
    public BookingSendingDto handleStatus(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam ApprovedState approved) {
        log.info("Request to handle booking with id={}", bookingId);
        BookingSendingDto handledBooking = bookingService.handleStatus(userId, bookingId, approved);
        if (handledBooking != null) {
            log.info("Successfully handled booking: {}", handledBooking);
        }
        return handledBooking;
    }

    /**
     * Получение бронирования по id
     *
     * @param userId    id польователя, запрашивающего бронирование
     * @param bookingId id бронирования
     * @return DTO полученной сущности
     */
    @GetMapping("/{bookingId}")
    public BookingSendingDto findById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        log.info("Request to get booking with id={}", bookingId);
        BookingSendingDto returnedBooking = bookingService.findById(userId, bookingId);
        if (returnedBooking != null) {
            log.info("Successfully returned booking with id={}", bookingId);
        }
        return returnedBooking;
    }

    /**
     * Поиск всех бронирований пользователя
     *
     * @param userId id бронирующего
     * @param state  параметр запроса (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @param from начальный индекс для пагинации
     * @param size размер страницы для пагинации
     * @return список DTO найденных сущностей
     */
    @GetMapping
    public List<BookingSendingDto> findAllCurrentUserBookingsByState(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get bookings with booker_id={}", userId);
        PaginationRequest paginationRequest = new PaginationRequest(from, size);
        return bookingService.findByBookerIdAndStatus(userId, state, paginationRequest);
    }

    /**
     * Поиск всех бронирований для вещей, которыми владеет пользователь
     *
     * @param userId id владельца
     * @param state  параметр запроса (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @param from начальный индекс для пагинации
     * @param size размер страницы для пагинации
     * @return список DTO найденных сущностей
     */
    @GetMapping("/owner")
    public List<BookingSendingDto> findAllOwnerBookingsByState(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get bookings with owner_id={}", userId);
        PaginationRequest paginationRequest = new PaginationRequest(from, size);
        return bookingService.findByOwnerIdAndStatus(userId, state, paginationRequest);
    }
}
