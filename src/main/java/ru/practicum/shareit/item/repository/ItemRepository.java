package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс-репозиторий для вещей
 */
public interface ItemRepository {
    Item add(Item item);

    Item update(long itemId, String name, String description, Boolean status);

    Item getById(Long itemId);

    List<Item> getAllByOwnerId(long ownerId);

    List<Item> searchAvailableBy(String text);
}
