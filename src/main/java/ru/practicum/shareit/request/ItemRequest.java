package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

/**
 * Класс запроса вещи для работы с БД
 */
@Data
@Builder
public class ItemRequest {
    private long id;
    private long userId;
    private String itemName;
    private String itemDescription;
}
