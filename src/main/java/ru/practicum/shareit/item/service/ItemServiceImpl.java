package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.exception.CommentCreationException;
import ru.practicum.shareit.item.exception.InvalidItemOwnerException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация бизнес-логики для вещей
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Item with id=%d is not found", itemId));
        }
        if (userId.equals(item.get().getOwner().getId())) {
            List<Booking> lastBooking = bookingRepository.findPastByItemId(itemId,
                    PageRequest.of(0, 1));
            List<Booking> nextBooking = bookingRepository.findFutureByItemId(itemId,
                    PageRequest.of(0, 1));
            return ItemMapper.toDtoWithBookings(item.get(),
                    lastBooking.size() != 1 ? null : ItemDtoWithBookings.BookingShort
                            .toBookingShort(lastBooking.get(0)),
                    nextBooking.size() != 1 ? null : ItemDtoWithBookings.BookingShort
                            .toBookingShort(nextBooking.get(0)));
        }
        return ItemMapper.toDtoWithBookings(item.get(), null, null);
    }

    @Override
    public List<ItemDtoWithBookings> findAllByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerIdWithBookings(ownerId);
    }

    @Override
    @Transactional
    public ItemDto add(Long ownerId, ItemDto itemDto) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id=%d is not found", ownerId));
        }
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(owner.get());
        Item returnedItem = itemRepository.save(newItem);
        return ItemMapper.toDto(returnedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Item with id=%d is not found", itemId));
        }
        Long ownerId = item.get().getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw new InvalidItemOwnerException(String.format("Owner in http header(%d) and in repository(%d) " +
                    "is not the same! Item id=%d", userId, ownerId, itemId));
        }
        Item returnedItem = updateRequiredFields(item.get(), itemDto);
        return ItemMapper.toDto(returnedItem);
    }

    private Item updateRequiredFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        return item;
    }

    @Override
    public List<ItemDto> searchAvailableBy(String text) {
        if (text == null || text.length() == 0) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableBy(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> booking = bookingRepository.findPastApprovedByBookerAndItem(userId, itemId,
                PageRequest.of(0, 1));
        if (booking.isEmpty()) {
            throw new CommentCreationException(String.format("User never booked an item with id=%d", itemId));
        }
        Comment newComment = CommentMapper.toComment(commentDto);
        newComment.setAuthor(booking.get(0).getBooker());
        newComment.setItem(booking.get(0).getItem());
        newComment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(newComment));
    }
}
