package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Юнит тесты для контроллера ItemRequestController
 */
@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class ItemRequestControllerTest {
    final MockMvc mvc;
    @MockBean
    final ItemRequestService itemRequestService;

    Long userId, itemId;
    ItemRequestDto itemRequestDto1, itemRequestDto2, itemRequestDto3;
    ItemRequestDto.ItemForRequest itemForRequest;
    DateTimeFormatter formatter;
    ObjectMapper mapper;

    @BeforeEach
    void init() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        userId = 1L;
        itemId = 1L;
        itemRequestDto1 = ItemRequestDto.builder()
                                        .id(1L)
                                        .created(LocalDateTime.now().plusDays(1))
                                        .description("Item request 1 description")
                                        .build();
        itemForRequest = ItemRequestDto.ItemForRequest.builder()
                                                      .requestId(itemRequestDto1.getId())
                                                      .id(itemId)
                                                      .available(true)
                                                      .ownerId(userId)
                                                      .name("Item 1")
                                                      .description("Item 1 description")
                                                      .build();
        itemRequestDto1.setItems(List.of(itemForRequest));
        itemRequestDto2 = ItemRequestDto.builder()
                                        .id(2L)
                                        .created(LocalDateTime.now().plusDays(1))
                                        .description("Item request 2 description")
                                        .build();

        itemRequestDto3 = ItemRequestDto.builder()
                                        .id(3L)
                                        .created(LocalDateTime.now().plusDays(1))
                                        .description("Item request 3 description")
                                        .build();
    }

    /**
     * Эндпоинт GET /requests, пустое тело, header X-Sharer-User-Id = userId
     *
     * @throws Exception
     */
    @Test
    void shouldGetAllByUserId() throws Exception {
        when(itemRequestService.findByUserId(userId)).thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mvc.perform(get("/requests").header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
           .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
           .andExpect(jsonPath("$[0].created", is(formatter.format(itemRequestDto1.getCreated()))))
           .andExpect(jsonPath("$[0].items[0].id", is(itemForRequest.getId()), Long.class))
           .andExpect(jsonPath("$[0].items[0].name", is(itemForRequest.getName())))
           .andExpect(jsonPath("$[0].items[0].description", is(itemForRequest.getDescription())))
           .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
           .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
           .andExpect(jsonPath("$[1].created", is(formatter.format(itemRequestDto2.getCreated()))));

        verify(itemRequestService, times(1)).findByUserId(userId);
    }

    /**
     * Эндпоинт GET /requests/all, пустое тело, header X-Sharer-User-Id = userId, from = 0, size = 10
     *
     * @throws Exception
     */
    @Test
    void shouldGetPageSortedByDate() throws Exception {
        PaginationRequest paginationRequest = new PaginationRequest(0, 10);
        when(itemRequestService.findPageSortedByDate(userId, paginationRequest)).thenReturn(List.of(itemRequestDto1,
                itemRequestDto2));

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
           .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
           .andExpect(jsonPath("$[0].created", is(formatter.format(itemRequestDto1.getCreated()))))
           .andExpect(jsonPath("$[0].items[0].id", is(itemForRequest.getId()), Long.class))
           .andExpect(jsonPath("$[0].items[0].name", is(itemForRequest.getName())))
           .andExpect(jsonPath("$[0].items[0].description", is(itemForRequest.getDescription())))
           .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
           .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
           .andExpect(jsonPath("$[1].created", is(formatter.format(itemRequestDto2.getCreated()))));

        verify(itemRequestService, times(1)).findPageSortedByDate(userId, paginationRequest);
    }

    /**
     * Эндпоинт GET /requests/{requestId}, пустое тело, header X-Sharer-User-Id = userId
     *
     * @throws Exception
     */
    @Test
    void shouldGetById() throws Exception {
        when(itemRequestService.findById(userId, itemRequestDto1.getId())).thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/" + itemRequestDto1.getId()).header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
           .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
           .andExpect(jsonPath("$.created", is(formatter.format(itemRequestDto1.getCreated()))))
           .andExpect(jsonPath("$.items[0].id", is(itemForRequest.getId()), Long.class))
           .andExpect(jsonPath("$.items[0].name", is(itemForRequest.getName())))
           .andExpect(jsonPath("$.items[0].description", is(itemForRequest.getDescription())));

        verify(itemRequestService, times(1)).findById(userId, itemRequestDto1.getId());
    }

    /**
     * Эндпоинт POST /requests, тело - DTO с запросом на вещь, header - X-Sharer-User-Id = userId
     * @throws Exception
     */
    @Test
    void shouldAdd() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description(itemRequestDto1.getDescription()).build();
        itemRequestDto1.setItems(new ArrayList<>());
        when(itemRequestService.add(userId, itemRequestDto)).thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                   .header("X-Sharer-User-Id", userId)
                   .content(mapper.writeValueAsString(itemRequestDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
           .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
           .andExpect(jsonPath("$.created", is(formatter.format(itemRequestDto1.getCreated()))));

        verify(itemRequestService, times(1)).add(userId, itemRequestDto);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemRequestService);
    }
}