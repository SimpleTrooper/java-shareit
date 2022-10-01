package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * DTO бронирования для отправки
 */
@Data
@Builder
public class BookingSendingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingItem item;
    private BookingUser booker;
    private BookingStatus status;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class BookingItem {
        private Long id;
        private String name;
        private String description;
        private Boolean available;

        public static BookingItem toBookingItem(Item item) {
            return BookingItem.builder()
                              .id(item.getId())
                              .name(item.getName())
                              .description(item.getDescription())
                              .available(item.getAvailable())
                              .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class BookingUser {
        private Long id;
        private String name;
        private String email;

        public static BookingUser toBookingUser(User user) {
            return BookingUser.builder()
                              .id(user.getId())
                              .name(user.getName())
                              .email(user.getEmail())
                              .build();
        }
    }
}
