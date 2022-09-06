package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Релизация репозитория вещей в памяти
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 0;

    @Override
    public Item getById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException(String.format("Item with id=%d is not found", itemId));
        }
        return new Item(item);
    }

    @Override
    public List<Item> getAllByOwnerId(long ownerId) {
        return items.values().stream()
                    .filter(item -> item.getOwnerId() == ownerId)
                    .collect(Collectors.toList());
    }

    @Override
    public Item add(Item item) {
        nextId++;
        Item newItem = new Item(item);
        newItem.setId(nextId);
        items.put(nextId, newItem);
        return new Item(newItem);
    }

    @Override
    public Item update(long itemId, String name, String description, Boolean available) {
        Item item = getById(itemId);
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
        items.put(itemId, item);
        return new Item(item);
    }

    @Override
    public List<Item> searchAvailableBy(String text) {
        String lowerText = text.toLowerCase();
        return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(lowerText)
                            || item.getDescription().toLowerCase().contains(lowerText))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
    }
}
