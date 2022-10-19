package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Юнит тесты сериализации BookingSendingDto
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingSendingDtoTest {
    final JacksonTester<BookingSendingDto> json;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void shouldSerialize() throws IOException {
        BookingSendingDto.BookingUser user = BookingSendingDto.BookingUser.builder()
                                              .id(1L)
                                              .name("username")
                                              .email("mail@mail.mail")
                                              .build();
        BookingSendingDto.BookingItem item = BookingSendingDto.BookingItem.builder()
                                              .id(1L)
                                              .name("item name")
                                              .description("item description")
                                              .available(true)
                                              .build();
        BookingSendingDto expected = BookingSendingDto.builder()
                                      .id(1L)
                                      .item(item)
                                      .booker(user)
                                      .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                      .end(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                                      .status(BookingStatus.APPROVED)
                                      .build();

        JsonContent<BookingSendingDto> actual = json.write(expected);

        assertThat(actual).extractingJsonPathNumberValue("$.id").isEqualTo(expected.getId().intValue());
        assertThat(actual).extractingJsonPathStringValue("$.start")
                          .isEqualTo(formatter.format(expected.getStart()));
        assertThat(actual).extractingJsonPathStringValue("$.end")
                          .isEqualTo(formatter.format(expected.getEnd()));
        assertThat(actual).extractingJsonPathNumberValue("$.item.id")
                          .isEqualTo(expected.getItem().getId().intValue());
        assertThat(actual).extractingJsonPathStringValue("$.item.name")
                          .isEqualTo(expected.getItem().getName());
        assertThat(actual).extractingJsonPathStringValue("$.item.description")
                          .isEqualTo(expected.getItem().getDescription());
        assertThat(actual).extractingJsonPathBooleanValue("$.item.available")
                          .isEqualTo(expected.getItem().getAvailable());
        assertThat(actual).extractingJsonPathNumberValue("$.booker.id")
                          .isEqualTo(expected.getBooker().getId().intValue());
        assertThat(actual).extractingJsonPathStringValue("$.booker.name")
                          .isEqualTo(expected.getBooker().getName());
        assertThat(actual).extractingJsonPathStringValue("$.booker.email")
                          .isEqualTo(expected.getBooker().getEmail());
    }
}