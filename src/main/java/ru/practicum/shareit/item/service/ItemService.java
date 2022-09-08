package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * Интерфейс бизнес-логики для вещей
 */
public interface ItemService {
    ItemDto getById(Long itemId);

    List<ItemDto> getAllByOwnerId(Long ownerId);

    ItemDto add(Long ownerId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    List<ItemDto> searchAvailableBy(String text);
}
