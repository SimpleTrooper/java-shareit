package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Интеграционные тесты для ItemRepository
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final BookingRepository bookingRepository;

    Item item1, item2;
    User user1, user2;
    Booking past, future;

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
                    .available(true)
                    .build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        past = Booking.builder()
                      .item(item1)
                      .start(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS))
                      .end(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                      .status(BookingStatus.APPROVED)
                      .booker(user2)
                      .build();
        future = Booking.builder()
                        .item(item1)
                        .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                        .status(BookingStatus.APPROVED)
                        .booker(user2)
                        .build();
        bookingRepository.save(past);
        bookingRepository.save(future);
    }

    /**
     * Стандартное поведение для searchAvailableBy
     */
    @Test
    void shouldSearchAvailableBy() {
        String text = "Item";
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.searchAvailableBy(text, PageRequest.of(0, 10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findAllByOwnerIdWithBookings
     */
    @Test
    void shouldFindAllByOwnerIdWithBookings() {
        List<ItemDtoWithBookings> expected = List.of(ItemMapper.toDtoWithBookings(item1,
                ItemDtoWithBookings.BookingShort.toBookingShort(past),
                ItemDtoWithBookings.BookingShort.toBookingShort(future)),
                ItemMapper.toDtoWithBookings(item2, null, null));

        List<ItemDtoWithBookings> actual = itemRepository.findAllByOwnerIdWithBookings(user1.getId(),
                PageRequest.of(0, 10));

        assertThat(actual, equalTo(expected));
    }
}