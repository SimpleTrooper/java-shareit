package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для запросов вещей
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequest> items = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ItemForRequest {
        private Long id;
        private String name;
        private Long ownerId;
        private String description;
        private Long requestId;
        private Boolean available;

        public static ItemForRequest toItemForRequest(Item item) {
            return ItemForRequest.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .ownerId(item.getOwner().getId())
                    .description(item.getDescription())
                    .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                    .available(item.getAvailable())
                    .build();
        }
    }
}
