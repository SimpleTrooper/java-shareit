package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для запросов вещей
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;
    private String itemDescription;
    private final LocalDateTime created;
}
