package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер ItemRequest - ItemRequestDto
 */
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(User requester, ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                          .id(itemRequestDto.getId())
                          .created(itemRequestDto.getCreated())
                          .description(itemRequestDto.getDescription())
                          .requester(requester)
                          .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                             .id(itemRequest.getId())
                             .created(itemRequest.getCreated())
                             .description(itemRequest.getDescription())
                             .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemRequestDto.ItemForRequest> itemsForRequest = items.stream()
                                                                   .map(ItemRequestDto.ItemForRequest::toItemForRequest)
                                                                   .collect(Collectors.toList());
        return ItemRequestDto.builder()
                             .id(itemRequest.getId())
                             .created(itemRequest.getCreated())
                             .description(itemRequest.getDescription())
                             .items(itemsForRequest)
                             .build();
    }
}
