package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Юнит тесты сериализации CommentDto
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoTest {
    final JacksonTester<CommentDto> json;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneId.of("GMT"));

    @Test
    void shouldSerialize() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                                          .id(1L)
                                          .authorName("author")
                                          .text("text")
                                          .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                          .build();

        JsonContent<CommentDto> actual = json.write(commentDto);

        assertThat(actual).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.authorName")
                          .isEqualTo("author");
        assertThat(actual).extractingJsonPathStringValue("$.created")
                          .isEqualTo(formatter.format(commentDto.getCreated()));
        assertThat(actual).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}