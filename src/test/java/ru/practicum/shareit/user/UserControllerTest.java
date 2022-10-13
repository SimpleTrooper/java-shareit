package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


/**
 * Юнит тесты для контроллера UserController
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class UserControllerTest {
    final MockMvc mvc;
    @MockBean
    final UserService userService;

    ObjectMapper mapper;
    UserDto userDto1, userDto2;

    @BeforeEach
    void init() {
        mapper = new ObjectMapper();

        userDto1 = UserDto.builder()
                          .id(1L)
                          .name("username1")
                          .email("email1@yandex.com")
                          .build();
        userDto2 = UserDto.builder()
                          .id(2L)
                          .name("username2")
                          .email("email2@yandex.com")
                          .build();
    }

    /**
     * Эндпоинт GET /users/{userId}, пустое тело
     *
     * @throws Exception
     */
    @Test
    void shouldGetById() throws Exception {
        when(userService.findById(userDto1.getId())).thenReturn(userDto1);
        UserDto expected = userDto1;

        mvc.perform(get("/users/" + userDto1.getId()))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(expected.getName())))
           .andExpect(jsonPath("$.email", is(expected.getEmail())));

        verify(userService, times(1)).findById(userDto1.getId());
    }

    /**
     * Эндпоинт GET /users, пустое тело
     *
     * @throws Exception
     */
    @Test
    void shouldGetAll() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto1, userDto2));
        List<UserDto> expected = List.of(userDto1, userDto2);

        mvc.perform(get("/users"))
           .andExpect(jsonPath("$[0].id", is(expected.get(0).getId()), Long.class))
           .andExpect(jsonPath("$[0].name", is(expected.get(0).getName())))
           .andExpect(jsonPath("$[0].email", is(expected.get(0).getEmail())))
           .andExpect(jsonPath("$[1].id", is(expected.get(1).getId()), Long.class))
           .andExpect(jsonPath("$[1].name", is(expected.get(1).getName())))
           .andExpect(jsonPath("$[1].email", is(expected.get(1).getEmail())));
        ;

        verify(userService, times(1)).findAll();
    }

    /**
     * Эндпоинт POST /users, тело - DTO пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldAdd() throws Exception {
        userDto1.setId(null);
        when(userService.add(userDto1)).thenReturn(userDto1);
        UserDto expected = userDto1;

        mvc.perform(post("/users")
                   .content(mapper.writeValueAsString(userDto1))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(expected.getName())))
           .andExpect(jsonPath("$.email", is(expected.getEmail())));

        verify(userService, times(1)).add(userDto1);
    }

    /**
     * Эндпоинт PATCH /users/{userId}, тело - DTO обновления пользователя
     *
     * @throws Exception
     */
    @Test
    void shouldUpdate() throws Exception {
        UserDto updateDto = UserDto.builder()
                                   .name("UpdatedName")
                                   .email("email@updated.com")
                                   .build();
        when(userService.update(userDto1.getId(), updateDto)).thenReturn(updateDto);

        mvc.perform(patch("/users/" + userDto1.getId())
                   .content(mapper.writeValueAsString(updateDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is(updateDto.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(updateDto.getName())))
           .andExpect(jsonPath("$.email", is(updateDto.getEmail())));

        verify(userService, times(1)).update(userDto1.getId(), updateDto);
    }

    /**
     * Эндпоинт DELETE /users/{userId}, пустое тело
     *
     * @throws Exception
     */
    @Test
    void shouldDelete() throws Exception {
        mvc.perform(delete("/users/" + userDto1.getId()));

        verify(userService, times(1)).delete(userDto1.getId());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userService);
    }
}