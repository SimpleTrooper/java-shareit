package ru.practicum.shareit.base.handler;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.base.exception.NotFoundException;
import ru.practicum.shareit.base.exception.ResourceAccessException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Юнит тесты для обработчика ошибок.
 * Используем эндпоинт GET /users/10
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ErrorHandlerTest {
    final MockMvc mvc;
    @MockBean
    UserService userService;

    /**
     * Тест NotFoundException
     *
     * @throws Exception
     */
    @Test
    void shouldReturn404WhenUserNotFoundForGetById() throws Exception {
        Long incorrectId = 10L;
        String test = "test";
        when(userService.findById(incorrectId)).thenThrow(new NotFoundException(test));

        mvc.perform(get("/users/" + incorrectId))
           .andExpect(jsonPath("$.error", is("Entity is not found: " + test)))
           .andExpect(jsonPath("$.statusCode", is(404)));

        verify(userService, times(1)).findById(incorrectId);
    }

    /**
     * Тест DataIntegrityViolationException
     *
     * @throws Exception
     */
    @Test
    void shouldReturn409WhenUniqueKeyIsDuplicated() throws Exception {
        Long incorrectId = 10L;
        String test = "test";
        when(userService.findById(incorrectId)).thenThrow(new DataIntegrityViolationException(test));

        mvc.perform(get("/users/" + incorrectId))
           .andExpect(jsonPath("$.error", is("Duplicate key conflict: " + test)))
           .andExpect(jsonPath("$.statusCode", is(409)));

        verify(userService, times(1)).findById(incorrectId);
    }

    /**
     * Тест ResourceAccessException
     *
     * @throws Exception
     */
    @Test
    void shouldReturn404WhenAccessIsForbidden() throws Exception {
        Long incorrectId = 10L;
        String test = "test";
        when(userService.findById(incorrectId)).thenThrow(new ResourceAccessException(test));

        mvc.perform(get("/users/" + incorrectId))
           .andExpect(jsonPath("$.error", is("Access is forbidden: " + test)))
           .andExpect(jsonPath("$.statusCode", is(404)));

        verify(userService, times(1)).findById(incorrectId);
    }

    /**
     * Тест ItemUnavailableException
     *
     * @throws Exception
     */
    @Test
    void shouldReturn400WhenItemIsUnavailable() throws Exception {
        Long incorrectId = 10L;
        String test = "test";
        when(userService.findById(incorrectId)).thenThrow(new ItemUnavailableException(test));

        mvc.perform(get("/users/" + incorrectId))
           .andExpect(jsonPath("$.error", is("Item is unavailable error: " + test)))
           .andExpect(jsonPath("$.statusCode", is(400)));

        verify(userService, times(1)).findById(incorrectId);
    }

    /**
     * Тест BookingStatusException
     *
     * @throws Exception
     */
    @Test
    void shouldReturn400WhenIllegalBookingStatus() throws Exception {
        Long incorrectId = 10L;
        String test = "test";
        when(userService.findById(incorrectId)).thenThrow(new BookingStatusException(test));

        mvc.perform(get("/users/" + incorrectId))
           .andExpect(jsonPath("$.error", is("Booking status error: " + test)))
           .andExpect(jsonPath("$.statusCode", is(400)));

        verify(userService, times(1)).findById(incorrectId);
    }
}