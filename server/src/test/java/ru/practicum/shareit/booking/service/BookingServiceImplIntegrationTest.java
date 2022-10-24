package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Интеграционные тесты для BookingServiceImpl
 */
@DataJpaTest
@Import(BookingServiceImpl.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    final BookingRepository bookingRepository;
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingService bookingService;

    Item item1, item2;
    User user1, user2;
    Booking booking1, booking2;

    @BeforeEach
    void init() {
        user1 = new User(null, "Username1", "mail1@yandex.mail");
        user2 = new User(null, "Username2", "mail2@yandex.mail");
        userRepository.save(user1);
        userRepository.save(user2);

        item1 = Item.builder()
                    .name("Item1")
                    .description("Item1 description")
                    .owner(user1)
                    .available(true)
                    .build();
        item2 = Item.builder()
                    .name("Item2")
                    .description("Item2 description")
                    .owner(user2)
                    .available(false)
                    .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        booking1 = Booking.builder()
                          .item(item1)
                          .start(LocalDateTime.now().minus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                          .end(LocalDateTime.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                          .status(BookingStatus.APPROVED)
                          .booker(user2)
                          .build();

        booking2 = Booking.builder()
                          .item(item1)
                          .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                          .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                          .status(BookingStatus.WAITING)
                          .booker(user2)
                          .build();
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        BookingReceivingDto toAdd = BookingReceivingDto.builder()
                                       .itemId(item1.getId())
                                       .start(LocalDateTime.now().plus(3, ChronoUnit.DAYS)
                                               .truncatedTo(ChronoUnit.SECONDS))
                                       .end(LocalDateTime.now().plus(3, ChronoUnit.DAYS)
                                               .truncatedTo(ChronoUnit.SECONDS))
                                       .build();

        BookingSendingDto expected = BookingSendingDto.builder()
                                      .item(BookingSendingDto.BookingItem.toBookingItem(item1))
                                      .booker(BookingSendingDto.BookingUser.toBookingUser(user2))
                                      .start(toAdd.getStart())
                                      .end(toAdd.getEnd())
                                      .build();

        BookingSendingDto actual = bookingService.add(user2.getId(), toAdd);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getItem(), equalTo(expected.getItem()));
        assertThat(actual.getBooker(), equalTo(expected.getBooker()));
        assertThat(actual.getStart(), equalTo(expected.getStart()));
        assertThat(actual.getEnd(), equalTo(expected.getEnd()));
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
    }

    /**
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        BookingSendingDto expected = BookingMapper.toSendingDto(booking2);

        BookingSendingDto actual = bookingService.findById(user1.getId(), booking2.getId());

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findByBookerIdAndStatus
     */
    @Test
    void shouldFindByBookerIdAndStatus() {
        List<BookingSendingDto> expected = List.of(BookingMapper.toSendingDto(booking2),
                BookingMapper.toSendingDto(booking1));

        List<BookingSendingDto> actual = bookingService.findByBookerIdAndStatus(user2.getId(),
                BookingRequestState.ALL, new PaginationRequest(0, 10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findByOwnerIdAndStatus
     */
    @Test
    void shouldFindByOwnerIdAndStatus() {
        List<BookingSendingDto> expected = List.of(BookingMapper.toSendingDto(booking2),
                BookingMapper.toSendingDto(booking1));

        List<BookingSendingDto> actual = bookingService.findByOwnerIdAndStatus(user1.getId(),
                BookingRequestState.ALL, new PaginationRequest(0, 10));

        assertThat(actual, equalTo(expected));
    }
}