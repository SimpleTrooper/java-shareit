package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Юнит тесты сериализации ItemRequestDto
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {
    final JacksonTester<ItemRequestDto> json;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void shouldSerialize() throws IOException {
        ItemRequestDto.ItemForRequest itemForRequest = ItemRequestDto.ItemForRequest.builder()
                                                                                    .id(1L)
                                                                                    .requestId(1L)
                                                                                    .name("Name")
                                                                                    .description("Description")
                                                                                    .available(true)
                                                                                    .ownerId(1L)
                                                                                    .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                                                      .id(1L)
                                                      .items(List.of(itemForRequest))
                                                      .created(LocalDateTime.now())
                                                      .description("Description for request")
                                                      .build();

        JsonContent<ItemRequestDto> actual = json.write(itemRequestDto);

        assertThat(actual).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.description")
                          .isEqualTo("Description for request");
        assertThat(actual).extractingJsonPathStringValue("$.created")
                          .isEqualTo(formatter.format(itemRequestDto.getCreated()));
        assertThat(actual).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(actual).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
        assertThat(actual).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Name");
        assertThat(actual).extractingJsonPathStringValue("$.items[0].description")
                          .isEqualTo("Description");
        assertThat(actual).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
    }
}