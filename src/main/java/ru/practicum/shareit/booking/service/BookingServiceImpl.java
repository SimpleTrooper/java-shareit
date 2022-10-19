package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.base.exception.IllegalRequestStateException;
import ru.practicum.shareit.booking.exception.BookingCreationException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация бизнес логики бронирований
 */
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingSendingDto add(Long bookerId, BookingReceivingDto bookingDto) {
        User booker = findUserByIdOrThrow(bookerId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Item with id=%d is not found",
                    bookingDto.getItemId()));
        }
        if (bookerId.equals(item.get().getOwner().getId())) {
            throw new BookingCreationException("Cannot create booking for item owner");
        }
        if (!item.get().getAvailable()) {
            throw new ItemUnavailableException(String.format("Item with id=%d is unavailable", item.get().getId()));
        }
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        newBooking.setBooker(booker);
        newBooking.setItem(item.get());
        newBooking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toSendingDto(bookingRepository.save(newBooking));
    }

    @Override
    @Transactional
    public BookingSendingDto handleStatus(Long userId, Long bookingId, ApprovedState approved) {
        findUserByIdOrThrow(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Booking with id=%d is not found", bookingId));
        }
        if (booking.get().getStatus() != BookingStatus.WAITING) {
            throw new BookingStatusException("Can't change status for non-WAITING booking");
        }
        Long itemId = booking.get().getItem().getId();
        Optional<Item> item = itemRepository.findById(itemId);
        Long ownerId = item.get().getOwner().getId();
        if (!userId.equals(ownerId)) {
            throw new InvalidItemOwnerException(String.format("User id=%d and owner id=%d is not the same!",
                    userId, ownerId));
        }
        BookingStatus status;
        switch (approved) {
            case TRUE:
                status = BookingStatus.APPROVED;
                break;
            case FALSE:
                status = BookingStatus.REJECTED;
                break;
            default:
                throw new IllegalRequestStateException(String.format("Status=%s can be only 'true' or 'false'",
                        approved));
        }
        booking.get().setStatus(status);
        return BookingMapper.toSendingDto(booking.get());
    }

    @Override
    public BookingSendingDto findById(Long userId, Long bookingId) {
        findUserByIdOrThrow(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Booking with id=%d is not found", bookingId));
        }
        if (!userId.equals(booking.get().getBooker().getId())) {
            Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
            if (!userId.equals(item.get().getOwner().getId())) {
                throw new InvalidItemOwnerException(String.format("User id=%d must be equal to owner id=%d " +
                                "or booker id=%d",
                        userId, item.get().getOwner().getId(), booking.get().getBooker().getId()));
            }
        }
        return BookingMapper.toSendingDto(booking.get());
    }

    @Override
    public List<BookingSendingDto> findByBookerIdAndStatus(Long bookerId, BookingRequestState state,
                                                           PaginationRequest paginationRequest) {
        findUserByIdOrThrow(bookerId);
        Pageable pageable = paginationRequest.makePaginationByFieldDesc("start");
        List<Booking> resultBookings = null;
        switch (state) {
            case ALL:
                resultBookings = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case WAITING:
                resultBookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                resultBookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED,
                        pageable);
                break;
            case CURRENT:
                resultBookings = bookingRepository.findAllByBookerWhereTimeIsInside(bookerId, LocalDateTime.now(),
                        pageable);
                break;
            case PAST:
                resultBookings = bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                resultBookings = bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(),
                        pageable);
                break;
        }
        return resultBookings.stream()
                             .map(BookingMapper::toSendingDto)
                             .sorted(Comparator.comparing(BookingSendingDto::getStart).reversed())
                             .collect(Collectors.toList());
    }

    @Override
    public List<BookingSendingDto> findByOwnerIdAndStatus(Long ownerId, BookingRequestState state,
                                                          PaginationRequest paginationRequest) {
        findUserByIdOrThrow(ownerId);
        List<Long> itemIds = itemRepository.findAllByOwnerId(ownerId)
                                           .stream()
                                           .map(Item::getId)
                                           .collect(Collectors.toList());
        Pageable pageable = paginationRequest.makePaginationByFieldDesc("start");
        List<Booking> resultBookings = null;
        switch (state) {
            case ALL:
                resultBookings = bookingRepository.findAllByItemIdIn(itemIds, pageable);
                break;
            case WAITING:
                resultBookings = bookingRepository.findAllByItemIdInAndStatus(itemIds, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                resultBookings = bookingRepository.findAllByItemIdInAndStatus(itemIds, BookingStatus.REJECTED,
                        pageable);
                break;
            case CURRENT:
                resultBookings = bookingRepository.findAllByItemIdInWhereTimeIsInside(itemIds, LocalDateTime.now(),
                        pageable);
                break;
            case PAST:
                resultBookings = bookingRepository.findAllByItemIdInAndEndBefore(itemIds, LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                resultBookings = bookingRepository.findAllByItemIdInAndStartAfter(itemIds, LocalDateTime.now(),
                        pageable);
                break;
        }
        return resultBookings.stream()
                             .map(BookingMapper::toSendingDto)
                             .sorted(Comparator.comparing(BookingSendingDto::getStart).reversed())
                             .collect(Collectors.toList());
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User " +
                "with id=%d is not found", userId)));
    }
}
