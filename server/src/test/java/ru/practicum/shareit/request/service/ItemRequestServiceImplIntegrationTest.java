package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Интеграционные тесты для ItemRequestServiceImpl
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ItemRequestServiceImpl.class})
public class ItemRequestServiceImplIntegrationTest {
    final ItemRequestService itemRequestService;
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final ItemRequestRepository itemRequestRepository;

    User user1, user2;
    ItemRequest itemRequest1, itemRequest2, itemRequest3;
    Item item1, item2, item3;

    @BeforeEach
    void init() {
        user1 = User.builder()
                    .name("User 1")
                    .email("user1@mail.email")
                    .build();
        user2 = User.builder()
                    .name("User 2")
                    .email("user2@mail.email")
                    .build();
        userRepository.save(user1);
        userRepository.save(user2);

        itemRequest1 = ItemRequest.builder()
                                  .requester(user1)
                                  .created(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                                  .description("Request 1 description")
                                  .build();
        itemRequest2 = ItemRequest.builder()
                                  .requester(user2)
                                  .created(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                                  .description("Request 2 description")
                                  .build();
        itemRequest3 = ItemRequest.builder()
                                  .requester(user2)
                                  .created(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                                  .description("Request 3 description")
                                  .build();
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);

        item1 = Item.builder()
                    .name("Item 1")
                    .description("Item 1 description")
                    .owner(user2)
                    .request(itemRequest1)
                    .available(true)
                    .build();
        item2 = Item.builder()
                    .name("Item 2")
                    .description("Item 2 description")
                    .owner(user2)
                    .available(false)
                    .build();
        item3 = Item.builder()
                    .name("Item 3")
                    .description("Item 3 description")
                    .owner(user1)
                    .request(itemRequest3)
                    .available(false)
                    .build();
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    /**
     * Стандартное поведение findByUserId
     */
    @Test
    void shouldFindByUserId() {
        List<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(itemRequest1, List.of(item1)));

        List<ItemRequestDto> actual = itemRequestService.findByUserId(user1.getId());

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findPageSortedByDate
     */
    @Test
    void shouldFindPageSortedByDate() {
        List<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(itemRequest3, List.of(item3)),
                ItemRequestMapper.toItemRequestDto(itemRequest2, new ArrayList<>()));

        List<ItemRequestDto> actual = itemRequestService.findPageSortedByDate(user1.getId(),
                new PaginationRequest(0, 10));

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(itemRequest3, List.of(item3));

        ItemRequestDto actual = itemRequestService.findById(user1.getId(), itemRequest3.getId());

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Description")
                .build();

        ItemRequestDto actual = itemRequestService.add(user1.getId(), itemRequestDto);

        assertThat(actual, notNullValue());
        assertThat(actual.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(actual.getCreated().truncatedTo(ChronoUnit.MINUTES),
                equalTo(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
    }
}
