package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация бизнес-логики для вещей
 */
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.getById(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.getAllByOwnerId(ownerId);
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto add(Long ownerId, ItemDto itemDto) {
        userRepository.getById(ownerId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwnerId(ownerId);
        Item returnedItem = itemRepository.add(newItem);
        return ItemMapper.toDto(returnedItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.getById(userId);
        Item item = itemRepository.getById(itemId);
        if (item.getOwnerId() != userId) {
            throw new InvalidItemOwnerException(String.format("Owner in http header(%d) and in repository(%d) " +
                    "is not the same! Item id=%d", userId, item.getOwnerId(), itemId));
        }
        Item returnedItem = itemRepository.update(itemId, itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable());
        return ItemMapper.toDto(returnedItem);
    }

    @Override
    public List<ItemDto> searchAvailableBy(String text) {
        if (text == null || text.length() == 0) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableBy(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
