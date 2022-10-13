package ru.practicum.shareit.booking.service.findbystatus;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты для методов findByBookerIdAndStatus и
 * findByOwnerIdAndStatus
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({BookingServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
public class BookingServiceImplFindByStatusTest {
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
    List<BookingSendingDto> expectedList;
    PaginationRequest paginationRequest;
    Pageable makedPageable;

    @BeforeEach
    void init() {
        paginationRequest = new PaginationRequest(0, 10);
        makedPageable = paginationRequest.makePaginationByFieldDesc("start");

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
        when(itemRepository.findAllByOwnerId(user1.getId())).thenReturn(List.of(item1));
        expectedList = List.of(BookingMapper.toSendingDto(booking2),
                BookingMapper.toSendingDto(booking1));
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для ALL
     */
    @Test
    void shouldFindByBookerIdAndStatusForAll() {
        List<BookingSendingDto> expected = List.of(BookingMapper.toSendingDto(booking2),
                BookingMapper.toSendingDto(booking1));
        PaginationRequest paginationRequest = new PaginationRequest(0, 10);
        Pageable makedPageable = paginationRequest.makePaginationByFieldDesc("start");
        when(bookingRepository.findAllByBookerId(user2.getId(), makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(), BookingRequestState.ALL,
                paginationRequest);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerId(user2.getId(), makedPageable);
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для WAITING
     */
    @Test
    void shouldFindByBookerIdAndStatusForWaiting() {
        when(bookingRepository.findAllByBookerIdAndStatus(user2.getId(), BookingStatus.WAITING, makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.WAITING, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(user2.getId(),
                BookingStatus.WAITING, makedPageable);
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для REJECTED
     */
    @Test
    void shouldFindByBookerIdAndStatusForRejected() {
        when(bookingRepository.findAllByBookerIdAndStatus(user2.getId(), BookingStatus.REJECTED, makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.REJECTED, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(user2.getId(),
                BookingStatus.REJECTED, makedPageable);
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для CURRENT
     */
    @Test
    void shouldFindByBookerIdAndStatusForCurrent() {
        when(bookingRepository.findAllByBookerWhereTimeIsInside(eq(user2.getId()), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.CURRENT, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerWhereTimeIsInside(eq(user2.getId()),
                any(), eq(makedPageable));
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для PAST
     */
    @Test
    void shouldFindByBookerIdAndStatusForPast() {
        when(bookingRepository.findAllByBookerIdAndEndBefore(eq(user2.getId()), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.PAST, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndBefore(eq(user2.getId()),
                any(), eq(makedPageable));
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus для FUTURE
     */
    @Test
    void shouldFindByBookerIdAndStatusForFuture() {
        when(bookingRepository.findAllByBookerIdAndStartAfter(eq(user2.getId()), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.FUTURE, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartAfter(eq(user2.getId()),
                any(), eq(makedPageable));
    }

    /**
     * Поведение findByBookerIdAndStatus при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForFindByBookerIdAndStatus() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.findByBookerIdAndStatus(incorrectId,
                BookingRequestState.ALL, new PaginationRequest(0, 1)));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для ALL
     */
    @Test
    void shouldFindByOwnerIdAndStatusForAll() {
        when(bookingRepository.findAllByItemIdIn(List.of(item1.getId()), makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(), BookingRequestState.ALL,
                paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1)).findAllByItemIdIn(List.of(item1.getId()),
                makedPageable);
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для WAITING
     */
    @Test
    void shouldFindByOwnerIdAndStatusForWaiting() {
        when(bookingRepository.findAllByItemIdInAndStatus(List.of(item1.getId()), BookingStatus.WAITING, makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.WAITING, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatus(List.of(item1.getId()),
                BookingStatus.WAITING, makedPageable);
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для REJECTED
     */
    @Test
    void shouldFindByOwnerIdAndStatusForRejected() {
        when(bookingRepository.findAllByItemIdInAndStatus(List.of(item1.getId()),
                BookingStatus.REJECTED, makedPageable))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.REJECTED, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatus(List.of(item1.getId()),
                BookingStatus.REJECTED, makedPageable);
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для CURRENT
     */
    @Test
    void shouldFindByOwnerIdAndStatusForCurrent() {
        when(bookingRepository.findAllByItemIdInWhereTimeIsInside(eq(List.of(item1.getId())), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.CURRENT, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByItemIdInWhereTimeIsInside(eq(List.of(item1.getId())), any(), eq(makedPageable));
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для PAST
     */
    @Test
    void shouldFindByOwnerIdAndStatusForPast() {
        when(bookingRepository.findAllByItemIdInAndEndBefore(eq(List.of(item1.getId())), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.PAST, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByItemIdInAndEndBefore(eq(List.of(item1.getId())), any(), eq(makedPageable));
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus для FUTURE
     */
    @Test
    void shouldFindByOwnerIdAndStatusForFuture() {
        when(bookingRepository.findAllByItemIdInAndStartAfter(eq(List.of(item1.getId())), any(), eq(makedPageable)))
                .thenReturn(List.of(booking1, booking2));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.FUTURE, paginationRequest);

        assertThat(actual, equalTo(expectedList));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerId(user1.getId());
        verify(bookingRepository, times(1))
                .findAllByItemIdInAndStartAfter(eq(List.of(item1.getId())), any(), eq(makedPageable));
    }

    /**
     * Поведение findByOwnerIdAndStatus при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForFindByOwnerIdAndStatus() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.findByOwnerIdAndStatus(incorrectId,
                BookingRequestState.ALL, new PaginationRequest(0, 1)));

        verify(userRepository, times(1)).findById(incorrectId);
    }
}
