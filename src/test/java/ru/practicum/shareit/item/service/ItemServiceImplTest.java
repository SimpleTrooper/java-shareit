package ru.practicum.shareit.item.service;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.exception.CommentCreationException;
import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты для ItemServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({ItemServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class ItemServiceImplTest {
    @MockBean
    final ItemRepository itemRepository;
    @MockBean
    final UserRepository userRepository;
    @MockBean
    final BookingRepository bookingRepository;
    @MockBean
    final CommentRepository commentRepository;
    @MockBean
    final ItemRequestRepository itemRequestRepository;
    @InjectMocks
    final ItemService itemService;

    Item item1, item2, item3;
    User user1, user2, user3;

    Booking future, past;

    @BeforeEach
    void init() {
        user1 = new User(1L, "Username1", "mail1@yandex.mail");
        user2 = new User(2L, "Username2", "mail2@yandex.mail");
        user3 = new User(3L, "Username3", "mail3@yandex.mail");
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));

        Comment comment = Comment.builder()
                                 .id(1L)
                                 .author(user1)
                                 .text("text")
                                 .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                 .build();
        item1 = Item.builder()
                    .id(1L)
                    .name("Item1")
                    .description("Item1 description")
                    .owner(user1)
                    .comments(List.of(comment))
                    .available(true)
                    .build();
        item2 = Item.builder()
                    .id(2L)
                    .name("Item2")
                    .description("Item2 description")
                    .owner(user2)
                    .available(false)
                    .build();
        item3 = Item.builder()
                    .id(3L)
                    .name("Item3")
                    .description("Item3 description")
                    .owner(user3)
                    .available(true)
                    .build();
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));
        when(itemRepository.findById(item3.getId())).thenReturn(Optional.of(item3));

        past = Booking.builder()
                      .id(1L)
                      .item(item1)
                      .start(LocalDateTime.now().minusDays(2))
                      .end(LocalDateTime.now().minusDays(1))
                      .status(BookingStatus.APPROVED)
                      .booker(user2)
                      .build();

        future = Booking.builder()
                        .id(2L)
                        .item(item1)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .status(BookingStatus.APPROVED)
                        .booker(user3)
                        .build();
        when(bookingRepository.findPastByItemId(item1.getId(),
                PageRequest.of(0, 1))).thenReturn(List.of(past));
        when(bookingRepository.findFutureByItemId(item1.getId(),
                PageRequest.of(0, 1))).thenReturn(List.of(future));
    }

    /**
     * Стандартное поведение findById, user - владелец вещи
     */
    @Test
    void shouldFindByIdForOwner() {
        ItemDtoWithBookings expected = ItemMapper.toDtoWithBookings(item1,
                ItemDtoWithBookings.BookingShort.toBookingShort(past),
                ItemDtoWithBookings.BookingShort.toBookingShort(future));

        ItemDtoWithBookings actual = itemService.findById(user1.getId(), item1.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(bookingRepository, times(1)).findPastByItemId(item1.getId(),
                PageRequest.of(0, 1));
        verify(bookingRepository, times(1)).findFutureByItemId(item1.getId(),
                PageRequest.of(0, 1));
    }

    /**
     * Стандартное поведение findById, user - не владелец вещи
     */
    @Test
    void shouldFindByIdForNonOwner() {
        ItemDtoWithBookings expected = ItemMapper.toDtoWithBookings(item1,
                null,
                null);

        ItemDtoWithBookings actual = itemService.findById(user2.getId(), item1.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Поведение findById при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForFindById() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemService.findById(incorrectId, item1.getId()));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение findById при некорректном itemId
     */
    @Test
    void shouldThrowWhenIncorrectItemIdForFindById() {
        Long incorrectId = -1L;
        when(itemRepository.findById(incorrectId)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(user1.getId(), incorrectId));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(incorrectId);
    }

    /**
     * Стандартное поведение findAllByOwnerId
     */
    @Test
    void shouldFindAllByOwnerId() {
        PaginationRequest request = new PaginationRequest(0, 10);
        when(itemRepository.findAllByOwnerIdWithBookings(user1.getId(), request.makePaginationByFieldAsc("id")))
                .thenReturn(List.of(ItemMapper.toDtoWithBookings(item1,
                        ItemDtoWithBookings.BookingShort.toBookingShort(past),
                        ItemDtoWithBookings.BookingShort.toBookingShort(future))));
        List<ItemDtoWithBookings> expected = List.of(ItemMapper.toDtoWithBookings(item1,
                ItemDtoWithBookings.BookingShort.toBookingShort(past),
                ItemDtoWithBookings.BookingShort.toBookingShort(future)));

        List<ItemDtoWithBookings> actual = itemService.findAllByOwnerId(user1.getId(), request);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findAllByOwnerIdWithBookings(item1.getId(),
                request.makePaginationByFieldAsc("id"));
    }

    /**
     * Поведение findAllByOwnerId при некорректном ownerId
     */
    @Test
    void shouldThrowWhenIncorrectOwnerIdForFindAllByOwnerId() {
        Long incorrectId = -1L;
        PaginationRequest request = new PaginationRequest(0, 10);
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.findAllByOwnerId(incorrectId, request));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Стандартное поведение add - добавление по запросу
     */
    @Test
    void shouldAddByRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                                             .id(1L)
                                             .created(LocalDateTime.now())
                                             .requester(user1)
                                             .build();
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        ItemDto toAdd = ItemDto.builder()
                               .name("New item")
                               .description("New item description")
                               .available(true)
                               .requestId(itemRequest.getId())
                               .build();
        Item newItem = ItemMapper.toItem(toAdd, user3, itemRequest);
        when(itemRepository.save(newItem)).thenReturn(newItem);
        ItemDto expected = ItemMapper.toDto(newItem);

        ItemDto actual = itemService.add(user3.getId(), toAdd);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user3.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verify(itemRepository, times(1)).save(newItem);
    }

    /**
     * Стандартное поведение add - добавление без запроса
     */
    @Test
    void shouldAddWithoutRequest() {
        ItemDto toAdd = ItemDto.builder()
                               .name("New item")
                               .description("New item description")
                               .available(true)
                               .build();
        Item newItem = ItemMapper.toItem(toAdd, user3, null);
        when(itemRepository.save(newItem)).thenReturn(newItem);
        ItemDto expected = ItemMapper.toDto(newItem);

        ItemDto actual = itemService.add(user3.getId(), toAdd);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user3.getId());
        verify(itemRepository, times(1)).save(newItem);
    }

    /**
     * Поведение add при некорректном ownerId
     */
    @Test
    void shouldThrowWhenIncorrectOwnerIdForAdd() {
        Long incorrectId = -1L;
        ItemDto toAdd = ItemDto.builder()
                               .name("New item")
                               .description("New item description")
                               .available(true)
                               .build();

        assertThrows(UserNotFoundException.class, () -> itemService.add(incorrectId, toAdd));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение add при некорректном requestId
     */
    @Test
    void shouldThrowWhenIncorrectRequestId() {
        Long incorrectId = -1L;
        when(itemRequestRepository.findById(incorrectId)).thenReturn(Optional.empty());
        ItemDto toAdd = ItemDto.builder()
                               .name("New item")
                               .description("New item description")
                               .available(true)
                               .requestId(incorrectId)
                               .build();

        assertThrows(ItemRequestNotFoundException.class, () -> itemService.add(user1.getId(), toAdd));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRequestRepository, times(1)).findById(toAdd.getRequestId());
    }

    /**
     * Стандартное поведение update
     */
    @Test
    void shouldUpdate() {
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .description("updated description")
                                   .available(false)
                                   .build();

        ItemDto actual = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(actual.getId(), equalTo(item1.getId()));
        assertThat(actual.getName(), equalTo(updateDto.getName()));
        assertThat(actual.getDescription(), equalTo(updateDto.getDescription()));
        assertThat(actual.getAvailable(), equalTo(updateDto.getAvailable()));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Стандартное поведение update - пустое поле name
     */
    @Test
    void shouldUpdateWithEmptyName() {
        ItemDto updateDto = ItemDto.builder()
                                   .description("updated description")
                                   .available(false)
                                   .build();
        String expectedName = item1.getName();

        ItemDto actual = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(actual.getId(), equalTo(item1.getId()));
        assertThat(actual.getName(), equalTo(expectedName));
        assertThat(actual.getDescription(), equalTo(updateDto.getDescription()));
        assertThat(actual.getAvailable(), equalTo(updateDto.getAvailable()));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Стандартное поведение update - пустое поле description
     */
    @Test
    void shouldUpdateWithEmptyDescription() {
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .available(false)
                                   .build();
        String expectedDescription = item1.getDescription();

        ItemDto actual = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(actual.getId(), equalTo(item1.getId()));
        assertThat(actual.getName(), equalTo(updateDto.getName()));
        assertThat(actual.getDescription(), equalTo(expectedDescription));
        assertThat(actual.getAvailable(), equalTo(updateDto.getAvailable()));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Стандартное поведение update - пустое поле available
     */
    @Test
    void shouldUpdateWithEmptyAvailable() {
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .description("updated description")
                                   .build();
        Boolean expectedAvailable = item1.getAvailable();

        ItemDto actual = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(actual.getId(), equalTo(item1.getId()));
        assertThat(actual.getName(), equalTo(updateDto.getName()));
        assertThat(actual.getDescription(), equalTo(updateDto.getDescription()));
        assertThat(actual.getAvailable(), equalTo(expectedAvailable));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Поведение update при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForUpdate() {
        Long incorrectId = -1L;
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .description("updated description")
                                   .available(false)
                                   .build();

        assertThrows(UserNotFoundException.class, () -> itemService.update(incorrectId, item1.getId(), updateDto));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение update при некорректном itemId
     */
    @Test
    void shouldThrowWhenIncorrectItemIdForUpdate() {
        Long incorrectId = -1L;
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .description("updated description")
                                   .available(false)
                                   .build();

        assertThrows(ItemNotFoundException.class, () -> itemService.update(user1.getId(), incorrectId, updateDto));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение update при userId != ownerId
     */
    @Test
    void shouldThrowWhenUserIdIsNotOwnerId() {
        ItemDto updateDto = ItemDto.builder()
                                   .name("updated name")
                                   .description("updated description")
                                   .available(false)
                                   .build();

        assertThrows(InvalidItemOwnerException.class, () -> itemService.update(user2.getId(), item1.getId(), updateDto));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
    }

    /**
     * Стандартное поведение searchAvailableBy
     */
    @Test
    void shouldSearchAvailableBy() {
        String text = "test search text";
        PaginationRequest request = new PaginationRequest(0, 10);
        when(itemRepository.searchAvailableBy(text, request.makePaginationByFieldAsc("id")))
                .thenReturn(List.of(item1, item2));
        List<ItemDto> expected = List.of(ItemMapper.toDto(item1), ItemMapper.toDto(item2));

        List<ItemDto> actual = itemService.searchAvailableBy(text, request);

        assertThat(actual, equalTo(expected));

        verify(itemRepository, times(1)).searchAvailableBy(text,
                request.makePaginationByFieldAsc("id"));
    }

    /**
     * Поведение searchAvailableBy при text = null
     */
    @Test
    void shouldReturnEmptyListWhenTextIsNullForSearch() {
        PaginationRequest request = new PaginationRequest(0, 10);
        List<ItemDto> expected = new ArrayList<>();

        List<ItemDto> actual = itemService.searchAvailableBy(null, request);

        assertThat(actual, equalTo(expected));
    }

    /**
     * Поведение searchAvailableBy при пустом text
     */
    @Test
    void shouldReturnEmptyListWhenTextIsEmptyForSearch() {
        PaginationRequest request = new PaginationRequest(0, 10);
        List<ItemDto> expected = new ArrayList<>();

        List<ItemDto> actual = itemService.searchAvailableBy("", request);

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение addComment
     */
    @Test
    void shouldAddComment() {
        CommentDto toAdd = CommentDto.builder()
                                     .text("Comment text")
                                     .build();
        when(bookingRepository.findPastApprovedByBookerAndItem(user2.getId(), item1.getId(),
                PageRequest.of(0, 1)))
                .thenReturn(List.of(past));

        CommentDto expected = CommentDto.builder()
                                        .authorName(user2.getName())
                                        .text(toAdd.getText())
                                        .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                        .build();
        Comment toSave = CommentMapper.toComment(expected);
        toSave.setAuthor(user2);
        toSave.setItem(item1);
        when(commentRepository.save(toSave)).thenReturn(toSave);

        CommentDto actual = itemService.addComment(user2.getId(), item1.getId(), toAdd);

        assertThat(actual, equalTo(expected));
        assertThat(actual.getCreated().truncatedTo(ChronoUnit.MINUTES),
                equalTo(expected.getCreated().truncatedTo(ChronoUnit.MINUTES)));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(bookingRepository, times(1)).findPastApprovedByBookerAndItem(user2.getId(),
                item1.getId(), PageRequest.of(0, 1));
        verify(commentRepository, times(1)).save(toSave);
    }

    /**
     * Поведение addComment при некорректном userId
     */
    @Test
    void shouldThrowWhenIncorrectUserIdForAddComment() {
        Long incorrectId = -1L;
        CommentDto toAdd = CommentDto.builder()
                                     .text("Comment text")
                                     .build();
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.addComment(incorrectId, item1.getId(), toAdd));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение addComment при некорректном itemId
     */
    @Test
    void shouldThrowWhenIncorrectItemIdForAddComment() {
        Long incorrectId = -1L;
        CommentDto toAdd = CommentDto.builder()
                                     .text("Comment text")
                                     .build();
        when(itemRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.addComment(user1.getId(), incorrectId, toAdd));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(itemRepository, times(1)).findById(incorrectId);
    }

    /**
     * Поведение addComment для пользователя, не бронировавшего вещь
     */
    @Test
    void shouldThrowWhenUserNotBookedItemForAddComment() {
        CommentDto toAdd = CommentDto.builder()
                                     .text("Comment text")
                                     .build();
        when(bookingRepository.findPastApprovedByBookerAndItem(user2.getId(), item1.getId(),
                PageRequest.of(0, 10))).thenReturn(new ArrayList<>());

        assertThrows(CommentCreationException.class, () -> itemService.addComment(user2.getId(), item1.getId(), toAdd));

        verify(userRepository, times(1)).findById(user2.getId());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(bookingRepository, times(1)).findPastApprovedByBookerAndItem(user2.getId(),
                item1.getId(), PageRequest.of(0, 1));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }
}