package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Юнит-тесты для UserDto
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {
    final JacksonTester<UserDto> json;

    @Test
    void shouldSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                                 .id(1L)
                                 .name("username")
                                 .email("mail@mail.ru")
                                 .build();

        JsonContent<UserDto> actual = json.write(userDto);

        assertThat(actual).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(actual).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(actual).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}