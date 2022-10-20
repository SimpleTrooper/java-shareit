package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.base.groups.OnCreate;
import ru.practicum.shareit.booking.dto.ApprovedState;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingRequestState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Контроллер для бронирований
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	public BookingController(BookingClient bookingClient) {
		this.bookingClient = bookingClient;
	}

	/**
	 * Добавление бронирования
	 *
	 * @param userId     id пользователя
	 * @param bookingDto DTO бронирования
	 * @return DTO добавленной сущности
	 */
	@PostMapping
	@Validated(value = OnCreate.class)
	public ResponseEntity<Object> add(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
							  @Valid @RequestBody BookingReceivingDto bookingDto) {
		log.info("Request to add new booking: {} from user: {}", bookingDto, userId);
		return bookingClient.add(userId, bookingDto);
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
	public ResponseEntity<Object> handleStatus(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
										  @PathVariable Long bookingId,
										  @RequestParam ApprovedState approved) {
		log.info("Request to handle booking with id={}", bookingId);
		return bookingClient.handleStatus(userId, bookingId, approved);
	}

	/**
	 * Получение бронирования по id
	 *
	 * @param userId    id польователя, запрашивающего бронирование
	 * @param bookingId id бронирования
	 * @return DTO полученной сущности
	 */
	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
									  @PathVariable Long bookingId) {
		log.info("Request to get booking with id={}", bookingId);
		return bookingClient.findById(userId, bookingId);
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
	public ResponseEntity<Object> findAllCurrentUserBookingsByState(
			@RequestHeader(name = "X-Sharer-User-Id") Long userId,
			@RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size) {
		log.info("Request to get bookings with booker_id={}", userId);
		return bookingClient.findByBookerIdAndStatus(userId, state, from, size);
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
	public ResponseEntity<Object> findAllOwnerBookingsByState(
			@RequestHeader(name = "X-Sharer-User-Id") Long userId,
			@RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size) {
		log.info("Request to get bookings with owner_id={}", userId);
		return bookingClient.findByOwnerIdAndStatus(userId, state, from, size);
	}
}
