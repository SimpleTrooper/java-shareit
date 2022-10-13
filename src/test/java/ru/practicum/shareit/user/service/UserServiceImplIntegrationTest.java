package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Интеграционные тесты для UserServiceImpl
 */
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserServiceImpl.class)
public class UserServiceImplIntegrationTest {
    final UserRepository userRepository;
    final UserService userService;

    User user1, user2, user3;

    @BeforeEach
    void init() {
        user1 = User.builder()
                    .name("Username1")
                    .email("mail1@mail.ru")
                    .build();
        user2 = User.builder()
                    .name("Username2")
                    .email("mail2@mail.ru")
                    .build();
        user3 = User.builder()
                    .name("Username3")
                    .email("mail3@mail.ru")
                    .build();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    /**
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        UserDto expected = UserMapper.toDto(user1);

        UserDto actual = userService.findById(user1.getId());

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение findAll
     */
    @Test
    void shouldFindAll() {
        List<UserDto> expected = List.of(UserMapper.toDto(user1), UserMapper.toDto(user2));

        List<UserDto> actual = userService.findAll();

        assertThat(actual, equalTo(expected));
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        UserDto toAdd = UserMapper.toDto(user3);

        UserDto actual = userService.add(toAdd);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getName(), equalTo(user3.getName()));
        assertThat(actual.getEmail(), equalTo(user3.getEmail()));
    }

    /**
     * Стандартное поведение update
     */
    @Test
    void shouldUpdate() {
        UserDto updateDto = UserDto.builder()
                                   .name("updated")
                                   .email("updated@mail.ru")
                                   .build();

        UserDto actual = userService.update(user1.getId(), updateDto);

        assertThat(actual, notNullValue());
        assertThat(actual.getId(), equalTo(user1.getId()));
        assertThat(actual.getName(), equalTo(updateDto.getName()));
        assertThat(actual.getEmail(), equalTo(updateDto.getEmail()));
    }

    /**
     * Стандартное поведение delete
     */
    @Test
    void shouldDelete() {
        userService.delete(user1.getId());

        assertThat(userRepository.findById(user1.getId()), equalTo(Optional.empty()));
    }
}
