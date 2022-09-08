package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Релизация репозитория вещей в памяти
 */
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new LinkedHashMap<>();
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
        Item updatedItem = items.computeIfPresent(itemId, (id, oldItem) -> {
            if (name != null) {
                oldItem.setName(name);
            }
            if (description != null) {
                oldItem.setDescription(description);
            }
            if (available != null) {
                oldItem.setAvailable(available);
            }
            return oldItem;
        });
        if (updatedItem == null) {
            throw new ItemNotFoundException(String.format("Item with id=%d is not found", itemId));
        }
        return new Item(updatedItem);
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
