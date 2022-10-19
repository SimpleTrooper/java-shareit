package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Юнит тесты сериализации ItemDtoWithBookings
 */
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoWithBookingsTest {
    final JacksonTester<ItemDtoWithBookings> json;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void shouldSerialize() throws IOException {
        List<ItemDto.ItemComment> comments = List.of(ItemDto.ItemComment.builder()
                                                .id(1L)
                                                .authorName("author")
                                                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                                                .text("text")
                                                .build());

        ItemDtoWithBookings.BookingShort lastBooking = ItemDtoWithBookings.BookingShort.builder()
                                               .id(1L)
                                               .bookerId(1L)
                                               .start(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS))
                                               .end((LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS)))
                                               .build();
        ItemDtoWithBookings.BookingShort nextBooking = ItemDtoWithBookings.BookingShort.builder()
                                               .id(2L)
                                               .bookerId(1L)
                                               .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                                               .end((LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS)))
                                               .build();
        ItemDtoWithBookings itemDtoWithBookings = ItemDtoWithBookings.builderWithBookings()
                                                                     .id(1L)
                                                                     .name("Item")
                                                                     .description("Description")
                                                                     .available(true)
                                                                     .requestId(1L)
                                                                     .lastBooking(lastBooking)
                                                                     .nextBooking(nextBooking)
                                                                     .comments(comments)
                                                                     .build();
        JsonContent<ItemDtoWithBookings> actual = json.write(itemDtoWithBookings);

        assertThat(actual).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(actual).extractingJsonPathStringValue("$.description")
                          .isEqualTo("Description");
        assertThat(actual).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(actual).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(actual).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(actual).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.lastBooking.start")
                          .isEqualTo(formatter.format(lastBooking.getStart()));
        assertThat(actual).extractingJsonPathStringValue("$.lastBooking.end")
                          .isEqualTo(formatter.format(lastBooking.getEnd()));
        assertThat(actual).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(actual).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.nextBooking.start")
                          .isEqualTo(formatter.format(nextBooking.getStart()));
        assertThat(actual).extractingJsonPathStringValue("$.nextBooking.end")
                          .isEqualTo(formatter.format(nextBooking.getEnd()));
        assertThat(actual).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(actual).extractingJsonPathStringValue("$.comments[0].authorName")
                          .isEqualTo("author");
        assertThat(actual).extractingJsonPathStringValue("$.comments[0].created")
                          .isEqualTo(formatter.format(comments.get(0).getCreated()));
        assertThat(actual).extractingJsonPathStringValue("$.comments[0].text")
                          .isEqualTo("text");
    }
}