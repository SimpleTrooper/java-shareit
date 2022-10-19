package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Интеграционные тесты для BookingRepository
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    User user1, user2;
    Item item1, item2;
    Booking booking1, booking2, booking3;

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
                    .owner(user1)
                    .available(false)
                    .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        booking1 = Booking.builder()
                      .item(item1)
                      .start(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS))
                      .end(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                      .status(BookingStatus.APPROVED)
                      .booker(user2)
                      .build();
        booking2 = Booking.builder()
                        .item(item1)
                        .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                        .status(BookingStatus.WAITING)
                        .booker(user2)
                        .build();
        booking3 = Booking.builder()
                          .item(item1)
                          .start(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                          .end(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.SECONDS))
                          .status(BookingStatus.APPROVED)
                          .booker(user2)
                          .build();
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    /**
     * Стандартное поведение для findAllByBookerWhereTimeIsInside
     */
    @Test
    void shouldFindAllByBookerWhereTimeIsInside() {
        List<Booking> expected = List.of(booking2);

        List<Booking> actual = bookingRepository.findAllByBookerWhereTimeIsInside(user2.getId(), LocalDateTime.now(),
                PageRequest.of(0,10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение для findAllByItemIdInWhereTimeIsInside
     */
    @Test
    void shouldFindAllByItemIdInWhereTimeIsInside() {
        List<Booking> expected = List.of(booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdInWhereTimeIsInside(List.of(item1.getId()),
                LocalDateTime.now(), PageRequest.of(0,10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение для findPastByItemId
     */
    @Test
    void shouldFindPastByItemId() {
        List<Booking> expected = List.of(booking1);

        List<Booking> actual = bookingRepository.findPastByItemId(item1.getId(), PageRequest.of(0,10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение для findFutureByItemId
     */
    @Test
    void shouldFindFutureByItemId() {
        List<Booking> expected = List.of(booking3);

        List<Booking> actual = bookingRepository.findFutureByItemId(item1.getId(), PageRequest.of(0,10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение для findPastApprovedByBookerAndItem
     */
    @Test
    void shouldFindPastApprovedByBookerAndItem() {
        List<Booking> expected = List.of(booking1);

        List<Booking> actual = bookingRepository.findPastApprovedByBookerAndItem(user2.getId(), item1.getId(),
                PageRequest.of(0,10));

        assertThat(actual, equalTo(expected));
    }
}