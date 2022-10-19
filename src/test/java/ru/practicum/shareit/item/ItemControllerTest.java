package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class ItemControllerTest {
    final MockMvc mvc;
    ObjectMapper mapper;
    @MockBean
    final ItemService itemService;

    ItemDto item1, item2;
    Long userId;
    DateTimeFormatter formatter;

    @BeforeEach
    void init() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        userId = 1L;
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        item1 = ItemDto.builder()
                       .id(1L)
                       .name("Item1")
                       .description("Item1 description")
                       .available(true)
                       .build();
        ItemDto.ItemComment comment = ItemDto.ItemComment.builder()
                                                         .id(1L)
                                                         .authorName("author")
                                                         .text("comment text")
                                                         .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                                         .build();
        item1.setComments(List.of(comment));
        item2 = ItemDto.builder()
                       .id(2L)
                       .name("Item2")
                       .description("Item2 description")
                       .available(false)
                       .build();
    }

    /**
     * Эндпоинт POST /items, тело - DTO вещи, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldAdd() throws Exception {
        ItemDto toAdd = ItemDto.builder()
                               .name("new item")
                               .description("new item description")
                               .available(true)
                               .build();
        ItemDto expected = ItemDto.builder()
                                  .id(1L)
                                  .name(toAdd.getName())
                                  .description(toAdd.getDescription())
                                  .available(toAdd.getAvailable())
                                  .build();
        when(itemService.add(userId, toAdd)).thenReturn(expected);

        mvc.perform(post("/items")
                   .content(mapper.writeValueAsString(toAdd))
                   .header("X-Sharer-User-Id", userId)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(expected.getName())))
           .andExpect(jsonPath("$.description", is(expected.getDescription())))
           .andExpect(jsonPath("$.available", is(expected.getAvailable())));

        verify(itemService, times(1)).add(userId, toAdd);
    }

    /**
     * Эндпоинт PATCH /items/{itemId}, тело - DTO обновленной вещи, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldUpdate() throws Exception {
        ItemDto toUpdate = ItemDto.builder()
                                  .name("new item")
                                  .description("new item description")
                                  .available(true)
                                  .build();
        ItemDto expected = ItemDto.builder()
                                  .id(1L)
                                  .name(toUpdate.getName())
                                  .description(toUpdate.getDescription())
                                  .available(toUpdate.getAvailable())
                                  .build();
        when(itemService.update(userId, item1.getId(), toUpdate)).thenReturn(expected);

        mvc.perform(patch("/items/" + item1.getId())
                   .content(mapper.writeValueAsString(toUpdate))
                   .header("X-Sharer-User-Id", userId)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(expected.getName())))
           .andExpect(jsonPath("$.description", is(expected.getDescription())))
           .andExpect(jsonPath("$.available", is(expected.getAvailable())));

        verify(itemService, times(1)).update(userId, item1.getId(), toUpdate);
    }

    /**
     * Эндпоинт GET /items/{itemId}, тело - пустое, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldGetById() throws Exception {
        ItemDto expected = ItemDto.builder()
                                  .id(item1.getId())
                                  .name(item1.getName())
                                  .description(item1.getDescription())
                                  .available(item1.getAvailable())
                                  .comments(item1.getComments())
                                  .build();
        ItemDtoWithBookings dtoWithBookings = ItemDtoWithBookings.builderWithBookings()
                                                                 .id(item1.getId())
                                                                 .name(item1.getName())
                                                                 .description(item1.getDescription())
                                                                 .available(item1.getAvailable())
                                                                 .comments(item1.getComments())
                                                                 .build();
        when(itemService.findById(userId, item1.getId())).thenReturn(dtoWithBookings);

        mvc.perform(get("/items/" + item1.getId()).header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(expected.getName())))
           .andExpect(jsonPath("$.description", is(expected.getDescription())))
           .andExpect(jsonPath("$.available", is(expected.getAvailable())))
           .andExpect(jsonPath("$.comments[0].id", is(item1.getComments().get(0).getId()), Long.class))
           .andExpect(jsonPath("$.comments[0].authorName", is(item1.getComments().get(0).getAuthorName())))
           .andExpect(jsonPath("$.comments[0].text", is(item1.getComments().get(0).getText())))
           .andExpect(jsonPath("$.comments[0].created",
                   is(formatter.format(item1.getComments().get(0).getCreated()))));

        verify(itemService, times(1)).findById(userId, item1.getId());
    }

    /**
     * Эндпоинт GET /items, тело - пустое, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldGetAllByOwnerId() throws Exception {
        List<ItemDtoWithBookings> expected = List.of(ItemDtoWithBookings.builderWithBookings()
                                                                        .id(item1.getId())
                                                                        .name(item1.getName())
                                                                        .description(item1.getDescription())
                                                                        .available(item1.getAvailable())
                                                                        .comments(item1.getComments())
                                                                        .build());
        ItemDtoWithBookings dtoWithBookings = ItemDtoWithBookings.builderWithBookings()
                                                                 .id(item1.getId())
                                                                 .name(item1.getName())
                                                                 .description(item1.getDescription())
                                                                 .available(item1.getAvailable())
                                                                 .comments(item1.getComments())
                                                                 .build();
        when(itemService.findAllByOwnerId(userId, new PaginationRequest(0, 10)))
                .thenReturn(List.of(dtoWithBookings));

        mvc.perform(get("/items").header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(expected.get(0).getId()), Long.class))
           .andExpect(jsonPath("$[0].name", is(expected.get(0).getName())))
           .andExpect(jsonPath("$[0].description", is(expected.get(0).getDescription())))
           .andExpect(jsonPath("$[0].available", is(expected.get(0).getAvailable())))
           .andExpect(jsonPath("$[0].comments[0].id", is(item1.getComments().get(0).getId()), Long.class))
           .andExpect(jsonPath("$[0].comments[0].authorName", is(item1.getComments().get(0).getAuthorName())))
           .andExpect(jsonPath("$[0].comments[0].text", is(item1.getComments().get(0).getText())))
           .andExpect(jsonPath("$[0].comments[0].created",
                   is(formatter.format(item1.getComments().get(0).getCreated()))));

        verify(itemService, times(1)).findAllByOwnerId(userId,
                new PaginationRequest(0, 10));
    }

    /**
     * Эндпоинт GET /items/search?text=text, тело - пустое, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldSearchBy() throws Exception {
        String text = "text";
        List<ItemDtoWithBookings> expected = List.of(ItemDtoWithBookings.builderWithBookings()
                                                                        .id(item1.getId())
                                                                        .name(item1.getName())
                                                                        .description(item1.getDescription())
                                                                        .available(item1.getAvailable())
                                                                        .comments(item1.getComments())
                                                                        .build());
        ItemDtoWithBookings dtoWithBookings = ItemDtoWithBookings.builderWithBookings()
                                                                 .id(item1.getId())
                                                                 .name(item1.getName())
                                                                 .description(item1.getDescription())
                                                                 .available(item1.getAvailable())
                                                                 .comments(item1.getComments())
                                                                 .build();
        when(itemService.searchAvailableBy(text, new PaginationRequest(0, 10)))
                .thenReturn(List.of(dtoWithBookings));

        mvc.perform(get("/items/search/").param("text", text)
                                         .header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(expected.get(0).getId()), Long.class))
           .andExpect(jsonPath("$[0].name", is(expected.get(0).getName())))
           .andExpect(jsonPath("$[0].description", is(expected.get(0).getDescription())))
           .andExpect(jsonPath("$[0].available", is(expected.get(0).getAvailable())))
           .andExpect(jsonPath("$[0].comments[0].id", is(item1.getComments().get(0).getId()), Long.class))
           .andExpect(jsonPath("$[0].comments[0].authorName", is(item1.getComments().get(0).getAuthorName())))
           .andExpect(jsonPath("$[0].comments[0].text", is(item1.getComments().get(0).getText())))
           .andExpect(jsonPath("$[0].comments[0].created",
                   is(formatter.format(item1.getComments().get(0).getCreated()))));

        verify(itemService, times(1)).searchAvailableBy(text,
                new PaginationRequest(0, 10));
    }

    /**
     * Эндпоинт POST /items/{itemId}/comment, тело - DTO комментария, header - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldAddComment() throws Exception {
        Long itemId = 1L;
        CommentDto toAdd = CommentDto.builder()
                                     .text("text")
                                     .build();
        CommentDto expected = CommentDto.builder()
                                        .id(1L)
                                        .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                        .authorName("author")
                                        .text(toAdd.getText())
                                        .build();
        when(itemService.addComment(userId, itemId, toAdd)).thenReturn(expected);

        mvc.perform(post("/items/" + itemId + "/comment").header("X-Sharer-User-Id", userId)
                                 .content(mapper.writeValueAsString(toAdd))
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.authorName", is(expected.getAuthorName())))
           .andExpect(jsonPath("$.text", is(expected.getText())))
           .andExpect(jsonPath("$.created",
                   is(formatter.format(expected.getCreated()))));

        verify(itemService, times(1)).addComment(userId, itemId, toAdd);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemService);
    }
}