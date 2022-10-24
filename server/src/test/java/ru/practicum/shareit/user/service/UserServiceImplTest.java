package ru.practicum.shareit.user.service;

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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Юнит тесты для UserServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig({UserServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.WARN)
class UserServiceImplTest {
    @MockBean
    final UserRepository userRepository;
    @InjectMocks
    final UserService userService;

    User user1, user2;

    @BeforeEach
    void init() {
        user1 = new User(1L, "Username1", "mail1@yandex.mail");
        user2 = new User(2L, "Username2", "mail2@yandex.mail");
    }

    /**
     * Стандартное поведение findById
     */
    @Test
    void shouldFindById() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        UserDto expected = UserMapper.toDto(user1);

        UserDto actual = userService.findById(user1.getId());

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findById(user1.getId());
    }

    /**
     * Поведение при некорректном userId
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectIdForFindById() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(incorrectId));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    /**
     * Стандартное поведение shouldFindAll
     */
    @Test
    void shouldFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> expected = List.of(UserMapper.toDto(user1), UserMapper.toDto(user2));

        List<UserDto> actual = userService.findAll();

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).findAll();
    }

    /**
     * Поведение при пустом userRepository
     */
    @Test
    void shouldReturnEmptyListForEmptyRepository() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<UserDto> expected = new ArrayList<>();

        List<UserDto> actual = userService.findAll();

        assertThat(expected, equalTo(actual));

        verify(userRepository, times(1)).findAll();
    }

    /**
     * Стандартное поведение add
     */
    @Test
    void shouldAdd() {
        UserDto userDtoToSave = UserMapper.toDto(user1);
        User userToSave = UserMapper.toUser(userDtoToSave);
        when(userRepository.save(any())).thenReturn(userToSave);
        UserDto expected = UserMapper.toDto(userToSave);

        UserDto actual = userService.add(userDtoToSave);

        assertThat(actual, equalTo(expected));

        verify(userRepository, times(1)).save(any());
    }

    /**
     * Стандартное поведение update
     */
    @Test
    void shouldUpdate() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        UserDto updateDto = UserDto.builder()
                                   .name("Newname")
                                   .email("newmail@mail.ru")
                                   .build();

        UserDto expected = UserDto.builder()
                                  .id(user1.getId())
                                  .name(updateDto.getName())
                                  .email(updateDto.getEmail())
                                  .build();

        UserDto actual = userService.update(user1.getId(), updateDto);

        assertThat(actual, equalTo(expected));
        assertThat(user1.getName(), equalTo(updateDto.getName()));
        assertThat(user1.getEmail(), equalTo(updateDto.getEmail()));

        verify(userRepository, times(1)).findById(user1.getId());
    }

    /**
     * Стандартное поведение update при пустом поле имени в Dto
     */
    @Test
    void shouldUpdateWithEmptyName() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        UserDto updateDto = UserDto.builder()
                                   .email("newmail@mail.ru")
                                   .build();

        UserDto expected = UserDto.builder()
                                  .id(user1.getId())
                                  .name(user1.getName())
                                  .email(updateDto.getEmail())
                                  .build();

        UserDto actual = userService.update(user1.getId(), updateDto);

        assertThat(actual, equalTo(expected));
        assertThat(user1.getEmail(), equalTo(updateDto.getEmail()));

        verify(userRepository, times(1)).findById(user1.getId());
    }

    /**
     * Стандартное поведение update при пустом email в Dto
     */
    @Test
    void shouldUpdateWithEmptyEmail() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        UserDto updateDto = UserDto.builder()
                                   .name("Newname")
                                   .build();

        UserDto expected = UserDto.builder()
                                  .id(user1.getId())
                                  .name(updateDto.getName())
                                  .email(user1.getEmail())
                                  .build();

        UserDto actual = userService.update(user1.getId(), updateDto);

        assertThat(actual, equalTo(expected));
        assertThat(user1.getName(), equalTo(updateDto.getName()));

        verify(userRepository, times(1)).findById(user1.getId());
    }

    /**
     * Поведение update при некорректном userId
     */
    @Test
    void shouldThrowNotFoundWhenIncorrectIdForUpdate() {
        Long incorrectId = -1L;
        when(userRepository.findById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(incorrectId, UserMapper.toDto(user1)));

        verify(userRepository, times(1)).findById(incorrectId);
    }

    @Test
    void shouldDelete() {
        userService.delete(user1.getId());

        verify(userRepository, times(1)).deleteById(user1.getId());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository);
    }
}