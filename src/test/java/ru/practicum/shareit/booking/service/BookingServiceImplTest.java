package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.exception.BookingCreationException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * Юнит тесты для BookingServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({BookingServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class BookingServiceImplTest {
    @MockBean
    final BookingRepository bookingRepository;
    @MockBean
    final ItemRepository itemRepository;
    @MockBean
    final UserRepository userRepository;
    @InjectMocks
    final BookingService bookingService;

    User user1, user2;
    Item item1, item2;
    Booking booking1, booking2;

    @BeforeEach
    void init() {
        user1 = new User(1L, "Username1", "mail1@yandex.mail");
        user2 = new User(2L, "Username2", "mail2@yandex.mail");
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        item1 = Item.builder()
                    .id(1L)
                    .name("Item1")
                    .description("Item1 description")
                    .owner(user1)
                    .available(true)
                    .build();
        item2 = Item.builder()
                    .id(2L)
                    .name("Item2")
                    .description("Item2 description")
                    .owner(user2)
                    .available(false)
                    .build();
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));

        booking1 = Booking.builder()
                          .id(1L)
                          .item(item1)
                          .start(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS))
                          .end(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                          .status(BookingStatus.APPROVED)
                          .booker(user2)
                          .build();

        booking2 = Booking.builder()
                          .id(2L)
                          .item(item1)
                          .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                          .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                          .status(BookingStatus.WAITING)
                          .booker(user2)
                          .build();
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(bookingRepository.findById(booking2.getId())).thenReturn(Optional.of(booking2));
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                                       .start(LocalDateTime.now()
                                                                           .plusDays(1)
                                                                           .truncatedTo(ChronoUnit.SECONDS))
                                                       .end(LocalDateTime.now()
                                                                         .plusDays(2)
                                                                         .truncatedTo(ChronoUnit.SECONDS))
                                                       .itemId(item1.getId())
                                                       .build();
        Booking expectedBooking = Booking.builder()
                                         .start(toAdd.getStart())
                                         .end(toAdd.getEnd())
                                         .status(BookingStatus.WAITING)
                                         .item(item1)
                                         .booker(user2)
                                         .build();
        BookingSendingDto expected = BookingMapper.toSendingDto(expectedBooking);
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        BookingSendingDto actual = bookingService.add(user2.getId(), toAdd);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    /**
     * Поведение add при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForAdd() {
        Long incorrectId = -1L;
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                                       .start(LocalDateTime.now()
                                                                           .plusDays(1)
                                                                           .truncatedTo(ChronoUnit.SECONDS))
                                                       .end(LocalDateTime.now()
                                                                         .plusDays(2)
                                                                         .truncatedTo(ChronoUnit.SECONDS))
                                                       .itemId(item1.getId())
                                                       .build();
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.add(incorrectId, toAdd));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение add при некорректном itemId
     */
    @Test
    void shouldThrowWhenIncorrectItemIdForAdd() {
        Long incorrectId = -1L;
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                                       .start(LocalDateTime.now()
                                                                           .plusDays(1)
                                                                           .truncatedTo(ChronoUnit.SECONDS))
                                                       .end(LocalDateTime.now()
                                                                         .plusDays(2)
                                                                         .truncatedTo(ChronoUnit.SECONDS))
                                                       .itemId(incorrectId)
                                                       .build();
        when(itemRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.add(user2.getId(), toAdd));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение add при userId = ownerId
     */
    @Test
    void shouldThrowWhenUserIsOwnerForAdd() {
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                                       .start(LocalDateTime.now()
                                                                           .plusDays(1)
                                                                           .truncatedTo(ChronoUnit.SECONDS))
                                                       .end(LocalDateTime.now()
                                                                         .plusDays(2)
                                                                         .truncatedTo(ChronoUnit.SECONDS))
                                                       .itemId(item1.getId())
                                                       .build();

        assertThrows(BookingCreationException.class, () -> bookingService.add(user1.getId(), toAdd));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Поведение add для недоступной вещи
     */
    @Test
    void shouldThrowWhenItemIsUnavailable() {
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                                       .start(LocalDateTime.now()
                                                                           .plusDays(1)
                                                                           .truncatedTo(ChronoUnit.SECONDS))
                                                       .end(LocalDateTime.now()
                                                                         .plusDays(2)
                                                                         .truncatedTo(ChronoUnit.SECONDS))
                                                       .itemId(item2.getId())
                                                       .build();

        assertThrows(ItemUnavailableException.class, () -> bookingService.add(user1.getId(), toAdd));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item2.getId());
    }

    /**
     * Стандартное поведение handleStatus
     */
    @Test
    void shouldHandleStatus() {
        BookingSendingDto expected = BookingMapper.toSendingDto(booking2);
        expected.setStatus(BookingStatus.APPROVED);

        BookingSendingDto actual = bookingService.handleStatus(user1.getId(), booking2.getId(), ApprovedState.TRUE);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
        verify(itemRepository, times(1)).findById(expected.getItem().getId());
    }

    /**
     * Поведение handleStatus при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForHandleStatus() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.handleStatus(incorrectId, booking2.getId(),
                ApprovedState.TRUE));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение handleStatus при некорректном bookingId
     */
    @Test
    void shouldThrowWhenIncorrectBookingIdForHandleStatus() {
        Long incorrectId = -1L;
        when(bookingRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.handleStatus(user1.getId(), incorrectId,
                ApprovedState.TRUE));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(bookingRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение handleStatus при статусе бронирования отличном от WAITING
     */
    @Test
    void shouldThrowWhenNonWaitingBookingStatusForHandleStatus() {
        booking2.setStatus(BookingStatus.APPROVED);

        assertThrows(BookingStatusException.class, () -> bookingService.handleStatus(user1.getId(), booking2.getId(),
                ApprovedState.TRUE));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
    }

    /**
     * Поведение handleStatus при user != owner
     */
    @Test
    void shouldThrowWhenUserIsNotOwnerForHandleStatus() {
        assertThrows(InvalidItemOwnerException.class, () -> bookingService.handleStatus(user2.getId(), booking2.getId(),
                ApprovedState.TRUE));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
        verify(itemRepository, times(1)).findById(booking2.getItem().getId());
    }

    /**
     * Стандартное поведение findById user == booker
     */
    @Test
    void shouldFindByIdForBooker() {
        BookingSendingDto expected = BookingMapper.toSendingDto(booking2);

        BookingSendingDto actual = bookingService.findById(user2.getId(), booking2.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
    }

    /**
     * Стандартное поведение findById user == owner
     */
    @Test
    void shouldFindByIdForOwner() {
        BookingSendingDto expected = BookingMapper.toSendingDto(booking2);

        BookingSendingDto actual = bookingService.findById(user1.getId(), booking2.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
        verify(itemRepository, times(1)).findById(booking2.getItem().getId());
    }

    /**
     * Поведение findById при user != owner || user != booker
     */
    @Test
    void shouldThrowForRandomUserForFindById() {
        User randomUser = new User(3L, "name", "random@mail.ru");
        when(userRepository.findById(randomUser.getId())).thenReturn(Optional.of(randomUser));

        assertThrows(InvalidItemOwnerException.class, () -> bookingService.findById(randomUser.getId(),
                booking2.getId()));

        verify(userRepository, times(1)).findById(randomUser.getId());
        verify(bookingRepository, times(1)).findById(booking2.getId());
        verify(itemRepository, times(1)).findById(booking2.getItem().getId());
    }

    /**
     * Поведение findById при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForFindById() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.findById(incorrectId,
                booking2.getId()));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение findById при некорректном bookingId
     */
    @Test
    void shouldThrowWhenIncorrectBookingIdForFindById() {
        Long incorrectId = -1L;
        when(bookingRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.findById(user1.getId(),
                incorrectId));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(bookingRepository, times(1)).findById(incorrectId);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(bookingRepository, itemRepository, userRepository);
    }
}