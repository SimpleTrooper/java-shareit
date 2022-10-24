package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingReceivingDto;
import ru.practicum.shareit.booking.dto.BookingSendingDto;
import ru.practicum.shareit.booking.model.ApprovedState;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Юнит тесты для BookingController
 */
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class BookingControllerTest {
    @MockBean
    final BookingService bookingService;
    final MockMvc mvc;
    ObjectMapper mapper;
    DateTimeFormatter formatter;

    Long itemId, userId;

    @BeforeEach
    void init() {
        itemId = 1L;
        userId = 1L;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("GMT"));
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());


    }

    /**
     * Эндпоинт POST /bookings, тело - DTO бронирования, X-Sharer-User-Id - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldAdd() throws Exception {
        Long bookingId = 1L;
        BookingReceivingDto receivingDto = BookingReceivingDto.builder()
                                              .start(LocalDateTime.now().plus(1, ChronoUnit.DAYS)
                                                      .truncatedTo(ChronoUnit.SECONDS))
                                              .end(LocalDateTime.now().plus(2, ChronoUnit.DAYS)
                                                      .truncatedTo(ChronoUnit.SECONDS))
                                              .itemId(itemId)
                                              .build();
        BookingSendingDto expected = BookingSendingDto.builder()
                                                      .id(bookingId)
                                                      .start(receivingDto.getStart())
                                                      .end(receivingDto.getEnd())
                                                      .status(BookingStatus.WAITING)
                                                      .build();
        when(bookingService.add(userId, receivingDto)).thenReturn(expected);

        mvc.perform(post("/bookings")
                   .header("X-Sharer-User-Id", userId)
                   .content(mapper.writeValueAsString(receivingDto))
                   .accept(MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(bookingId), Long.class))
           .andExpect(jsonPath("$.start", is(formatter.format(expected.getStart()))))
           .andExpect(jsonPath("$.end", is(formatter.format(expected.getEnd()))))
           .andExpect(jsonPath("$.status", is(expected.getStatus().toString())));

        verify(bookingService, times(1)).add(userId, receivingDto);
    }

    /**
     * Эндпоинт PATCH /bookings/{bookingId}/?approved={state}, тело - пустое, X-Sharer-User-Id - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldHandleStatus() throws Exception {
        Long bookingId = 1L;
        ApprovedState state = ApprovedState.TRUE;
        BookingSendingDto expected = BookingSendingDto.builder()
                                                      .id(bookingId)
                                                      .status(BookingStatus.APPROVED)
                                                      .build();
        when(bookingService.handleStatus(userId, bookingId, state)).thenReturn(expected);

        mvc.perform(patch("/bookings/" + bookingId)
                   .header("X-Sharer-User-Id", userId)
                   .param("approved", "TRUE"))
           .andExpect(jsonPath("$.id", is(bookingId), Long.class))
           .andExpect(jsonPath("$.status", is(expected.getStatus().toString())));

        verify(bookingService, times(1)).handleStatus(userId, bookingId, state);
    }

    /**
     * Эндпоинт GET /bookings/{bookingId}, тело - пустое, X-Sharer-User-Id - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldFindById() throws Exception {
        Long bookingId = 1L;
        BookingSendingDto expected = BookingSendingDto.builder()
                                                      .id(bookingId)
                                                      .status(BookingStatus.WAITING)
                                                      .build();
        when(bookingService.findById(userId, bookingId)).thenReturn(expected);

        mvc.perform(get("/bookings/" + bookingId)
                   .header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$.id", is(bookingId), Long.class))
           .andExpect(jsonPath("$.status", is(expected.getStatus().toString())));

        verify(bookingService, times(1)).findById(userId, bookingId);
    }

    /**
     * Эндпоинт GET /bookings, тело - пустое, X-Sharer-User-Id - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldFindAllCurrentUserBookingsByState() throws Exception {
        Long bookingId = 1L;
        BookingRequestState state = BookingRequestState.ALL;
        PaginationRequest paginationRequest = new PaginationRequest(0, 10);
        List<BookingSendingDto> expected = List.of(BookingSendingDto.builder()
                                                                    .id(bookingId)
                                                                    .status(BookingStatus.WAITING)
                                                                    .build());
        when(bookingService.findByBookerIdAndStatus(userId, state, paginationRequest))
                .thenReturn(expected);

        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
           .andExpect(jsonPath("$[0].status", is(expected.get(0).getStatus().toString())));

        verify(bookingService, times(1)).findByBookerIdAndStatus(userId, state,
                paginationRequest);
    }

    /**
     * Эндпоинт GET /bookings/owner, тело - пустое, X-Sharer-User-Id - id пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldFindAllOwnerBookingsByState() throws Exception {
        Long bookingId = 1L;
        BookingRequestState state = BookingRequestState.ALL;
        PaginationRequest paginationRequest = new PaginationRequest(0, 10);
        List<BookingSendingDto> expected = List.of(BookingSendingDto.builder()
                                                                    .id(bookingId)
                                                                    .status(BookingStatus.WAITING)
                                                                    .build());
        when(bookingService.findByOwnerIdAndStatus(userId, state, paginationRequest))
                .thenReturn(expected);

        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", userId))
           .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
           .andExpect(jsonPath("$[0].status", is(expected.get(0).getStatus().toString())));

        verify(bookingService, times(1)).findByOwnerIdAndStatus(userId, state,
                paginationRequest);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(bookingService);
    }
}