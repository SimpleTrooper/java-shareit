package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

/**
 * Интерфейс бизнес-логики для вещей
 */
public interface ItemService {
    ItemDto findById(Long userId, Long itemId);

    List<ItemDtoWithBookings> findAllByOwnerId(Long ownerId);

    ItemDto add(Long ownerId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    List<ItemDto> searchAvailableBy(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
