package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс запроса вещи для работы с БД
 */
@Data
@Builder
@AllArgsConstructor
public class ItemRequest {
    private long id;
    private final long requesterId;
    private String itemDescription;
    private final LocalDateTime created;

    public ItemRequest(ItemRequest request) {
        this.id = request.id;
        this.requesterId = request.requesterId;
        this.itemDescription = request.itemDescription;
        this.created = request.created;
    }
}
