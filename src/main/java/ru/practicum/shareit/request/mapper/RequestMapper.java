package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * Маппер ItemRequest - ItemRequestDto
 */
public class RequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                          .id(itemRequestDto.getId())
                          .itemName(itemRequestDto.getItemName())
                          .itemDescription(itemRequestDto.getItemDescription())
                          .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                             .id(itemRequest.getId())
                             .itemName(itemRequest.getItemName())
                             .itemDescription(itemRequest.getItemDescription())
                             .build();
    }
}
