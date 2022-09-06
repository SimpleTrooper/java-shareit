package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для запросов вещей
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;
    private String itemName;
    private String itemDescription;
}
