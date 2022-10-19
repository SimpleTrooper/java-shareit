package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.base.validation.groups.OnCreate;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
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
    @Null(groups = OnCreate.class)
    private Long id;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @Null(groups = OnCreate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
