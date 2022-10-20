package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тесты для ItemRequestRepository
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;

    ItemRequest itemRequest1, itemRequest2;
    User requester1, requester2;

    @BeforeEach
    void init() {
        requester1 = User.builder()
                         .name("Requester1")
                         .email("requester1@mail.mail")
                         .build();
        requester2 = User.builder()
                         .name("Requester2")
                         .email("requester2@mail.mail")
                         .build();
        userRepository.save(requester1);
        userRepository.save(requester2);
        itemRequest1 = ItemRequest.builder()
                                  .requester(requester1)
                                  .created(LocalDateTime.now())
                                  .description("Item description 1")
                                  .build();
        itemRequest2 = ItemRequest.builder()
                                  .requester(requester2)
                                  .created(LocalDateTime.now())
                                  .description("Item description 2")
                                  .build();
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void shouldFindAllByRequesterId() {
        List<ItemRequest> expected = List.of(itemRequest1);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequesterId(requester1.getId());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void shouldFindAllByRequesterIdNot() {
        List<ItemRequest> expected = List.of(itemRequest2);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequesterIdNot(requester1.getId(),
                PageRequest.of(0, 1));

        assertThat(actual, equalTo(expected));
    }
}