package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юнит тесты для ItemRequestServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ItemRequestServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class ItemRequestServiceImplTest {
    @MockBean
    final ItemRequestRepository itemRequestRepository;
    @MockBean
    final UserRepository userRepository;
    @MockBean
    final ItemRepository itemRepository;
    @InjectMocks
    final ItemRequestService itemRequestService;

    Long userId, ownerId;
    User user, owner;
    ItemRequest itemRequest1, itemRequest2;
    Item item1, item2;
    PaginationRequest paginationRequest;

    @BeforeEach
    void init() {
        paginationRequest = new PaginationRequest(0, 10);
        userId = 1L;
        ownerId = 2L;
        user = new User(userId, "Username 1", "usermail1@mail.mail");
        owner = new User(ownerId, "Ownername 2", "ownermail@mail.mail");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        itemRequest1 = ItemRequest.builder()
                                  .id(1L)
                                  .requester(user)
                                  .description("Request for item 1")
                                  .created(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                                  .build();
        itemRequest2 = ItemRequest.builder()
                                  .id(2L)
                                  .requester(user)
                                  .description("Request for item 2")
                                  .created(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
                                  .build();
        item1 = Item.builder()
                    .id(1L)
                    .name("Item 1")
                    .description("Item 1 description")
                    .available(true)
                    .request(itemRequest1)
                    .owner(owner)
                    .build();
        item2 = Item.builder()
                    .id(1L)
                    .name("Item 2")
                    .description("Item 2 description")
                    .available(true)
                    .request(itemRequest2)
                    .owner(owner)
                    .build();
        when(itemRepository.findAllByRequestId(itemRequest1.getId())).thenReturn(List.of(item1));
        when(itemRepository.findAllByRequestId(itemRequest2.getId())).thenReturn(new ArrayList<>());
        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRequestRepository.findAllByRequesterId(ownerId)).thenReturn(new ArrayList<>());
    }

    /**
     * Стандартное поведение findByUserId()
     */
    @Test
    void shouldFindByUserId() {
        List<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(itemRequest1, List.of(item1)),
                ItemRequestMapper.toItemRequestDto(itemRequest2, new ArrayList<>()));

        List<ItemRequestDto> actual = itemRequestService.findByUserId(userId);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findAllByRequesterId(userId);
        verify(itemRepository, times(expected.size())).findAllByRequestId(any());
    }

    /**
     * Поведение при некорректном id findByUserId()
     */
    @Test
    void shouldThrowNotFoundForFindByUserIdWhenIncorrectId() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findByUserId(incorrectId));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение при пустом списке запросов
     */
    @Test
    void shouldReturnEmptyListWhenNoRequestsForFindByUserId() {
        List<ItemRequestDto> expected = new ArrayList<>();

        List<ItemRequestDto> actual = itemRequestService.findByUserId(ownerId);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRequestRepository, times(1)).findAllByRequesterId(ownerId);
        verify(itemRepository, times(expected.size())).findAllByRequestId(any());
    }

    /**
     * Стандартное поведение findPageSortedByDate
     */
    @Test
    void shouldReturnAllFindPageSortedByDate() {
        List<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(itemRequest2, new ArrayList<>()),
                ItemRequestMapper.toItemRequestDto(itemRequest1, List.of(item1)));
        when(itemRequestRepository.findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created")))
                .thenReturn(List.of(itemRequest2, itemRequest1));

        List<ItemRequestDto> actual = itemRequestService.findPageSortedByDate(ownerId, paginationRequest);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created"));
        verify(itemRepository, times(expected.size())).findAllByRequestId(any());
    }

    /**
     * Поведение при смещении from = 1, size = 2
     */
    @Test
    void shouldReturnRequest2WhenFrom1AndSize2ForFindPageSortedByDate() {
        List<ItemRequestDto> expected = List.of(ItemRequestMapper.toItemRequestDto(itemRequest2, new ArrayList<>()));
        paginationRequest = new PaginationRequest(1, 2);
        when(itemRequestRepository.findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created")))
                .thenReturn(List.of(itemRequest2));

        List<ItemRequestDto> actual = itemRequestService.findPageSortedByDate(ownerId, paginationRequest);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created"));
        verify(itemRepository, times(expected.size())).findAllByRequestId(any());
    }

    /**
     * Поведение при некорректном id пользователя
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectIdForFindPageSortedByDate() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findPageSortedByDate(incorrectId, any()));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение при некорректном pagination
     */
    @Test
    void shouldReturnEmptyListWhenIncorrectPageForFindPageSortedByDate() {
        List<ItemRequestDto> expected = new ArrayList<>();
        paginationRequest = new PaginationRequest(-1, 2);
        when(itemRequestRepository.findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created")))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> actual = itemRequestService.findPageSortedByDate(ownerId, paginationRequest);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(ownerId,
                paginationRequest.makePaginationByFieldDesc("created"));
        verify(itemRepository, times(expected.size())).findAllByRequestId(any());
    }

    /**
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        when(itemRequestRepository.findById(itemRequest1.getId())).thenReturn(Optional.of(itemRequest1));
        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(itemRequest1, List.of(item1));

        ItemRequestDto actual = itemRequestService.findById(userId, itemRequest1.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(itemRequest1.getId());
        verify(itemRepository, times(1)).findAllByRequestId(itemRequest1.getId());
    }

    /**
     * Поведение при некорректном requestId метода findById
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectRequestIdForFindById() {
        Long incorrectId = -1L;
        when(itemRequestRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.findById(userId, incorrectId));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение при некорректном userId метода findById
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectUserIdForFindById() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.findById(incorrectId,
                itemRequest1.getId()));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        itemRequest1.setRequester(user);
        itemRequest1.setCreated(LocalDateTime.now());
        when(itemRequestRepository.save(itemRequest1)).thenReturn(itemRequest1);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest1);

        ItemRequestDto actual = itemRequestService.add(userId, itemRequestDto);

        assertThat(actual, equalTo(itemRequestDto));
        assertThat(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                equalTo(actual.getCreated().truncatedTo(ChronoUnit.MINUTES)));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(itemRequest1);
    }

    /**
     * Поведение add при некорректном userId
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectIdForAdd() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.add(incorrectId,
                ItemRequestMapper.toItemRequestDto(itemRequest1)));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, itemRepository, itemRequestRepository);
    }
}