package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@Import(ItemServiceImpl.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    final ItemRepository itemRepository;
    final UserRepository userRepository;
    final BookingRepository bookingRepository;
    final CommentRepository commentRepository;
    final ItemRequestRepository itemRequestRepository;
    final ItemService itemService;

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
                    .owner(user2)
                    .available(false)
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
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        ItemDtoWithBookings expected = ItemMapper.toDtoWithBookings(item1,
                ItemDtoWithBookings.BookingShort.toBookingShort(past),
                ItemDtoWithBookings.BookingShort.toBookingShort(future));

        ItemDtoWithBookings actual = itemService.findById(user1.getId(), item1.getId());

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findAllByOwnerId
     */
    @Test
    void shouldFindAllByOwnerId() {
        List<ItemDtoWithBookings> expected = List.of(ItemMapper.toDtoWithBookings(item1,
                ItemDtoWithBookings.BookingShort.toBookingShort(past),
                ItemDtoWithBookings.BookingShort.toBookingShort(future)));

        List<ItemDtoWithBookings> actual = itemService.findAllByOwnerId(user1.getId(),
                new PaginationRequest(0, 10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        ItemRequest newRequest = ItemRequest.builder()
                                            .requester(user1)
                                            .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                            .description("new request")
                                            .build();
        itemRequestRepository.save(newRequest);
        ItemDto toAdd = ItemDto.builder()
                               .name("new item name")
                               .description("new item description")
                               .available(true)
                               .requestId(newRequest.getId())
                               .build();
        ItemDto expected = ItemDto.builder()
                                  .name(toAdd.getName())
                                  .description(toAdd.getDescription())
                                  .requestId(newRequest.getId())
                                  .available(toAdd.getAvailable())
                                  .build();

        ItemDto actual = itemService.add(user2.getId(), toAdd);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getName(), equalTo(expected.getName()));
        assertThat(actual.getDescription(), equalTo(expected.getDescription()));
        assertThat(actual.getAvailable(), equalTo(expected.getAvailable()));
        assertThat(actual.getRequestId(), equalTo(expected.getRequestId()));
    }

    /**
     * Стандартное поведение update
     */
    @Test
    void shouldUpdate() {
        ItemDto toUpdate = ItemDto.builder()
                                  .name("new item name")
                                  .description("new item description")
                                  .available(true)
                                  .build();

        ItemDto expected = ItemDto.builder()
                                  .id(item1.getId())
                                  .name(toUpdate.getName())
                                  .description(toUpdate.getDescription())
                                  .available(toUpdate.getAvailable())
                                  .build();

        ItemDto actual = itemService.update(user1.getId(), item1.getId(), toUpdate);

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение searchAvailableBy
     */
    @Test
    void shouldSearchAvailableBy() {
        List<ItemDto> expected = List.of(ItemMapper.toDto(item1));

        List<ItemDto> actual = itemService.searchAvailableBy("Item", new PaginationRequest(0, 10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение addComment
     */
    @Test
    void shouldAddComment() {
        CommentDto toAdd = CommentDto.builder()
                                     .text("new Comment")
                                     .build();
        CommentDto expected = CommentDto.builder()
                                        .text(toAdd.getText())
                                        .authorName(user2.getName())
                                        .created(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                                        .build();

        CommentDto actual = itemService.addComment(user2.getId(), item1.getId(), toAdd);

        assertThat(actual, notNullValue());
        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getCreated().truncatedTo(ChronoUnit.MINUTES), equalTo(expected.getCreated()));
        assertThat(actual.getAuthorName(), equalTo(expected.getAuthorName()));
        assertThat(actual.getText(), equalTo(expected.getText()));
    }
}