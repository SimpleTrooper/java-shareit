package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Класс вещи для работы с БД
 */
@Data
@AllArgsConstructor
@Builder
public class Item {
    private long id;
    private long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private final ItemRequest request;

    public Item(Item item) {
        this.id = item.id;
        this.ownerId = item.ownerId;
        this.name = item.name;
        this.description = item.description;
        this.available = item.available;
        this.request = item.request == null ? null : item.request;
    }
}
