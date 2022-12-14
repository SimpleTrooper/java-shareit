package ru.practicum.shareit.item.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация кастомного интерфейса - добавление полей Booking в ItemDTO
 */
public class ItemRepositoryWithBookingsImpl implements ItemRepositoryWithBookings {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public ItemRepositoryWithBookingsImpl(@Lazy ItemRepository itemRepository,
                                          @Lazy BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<ItemDtoWithBookings> findAllByOwnerIdWithBookings(Long ownerId, Pageable pageable) {
        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(ownerId, pageable);
        return itemsByOwner.stream()
                           .map(item -> {
                               List<Booking> lastBooking = bookingRepository.findPastByItemId(item.getId(),
                                       PageRequest.of(0, 1));
                               List<Booking> nextBooking = bookingRepository.findFutureByItemId(item.getId(),
                                       PageRequest.of(0, 1));
                               return ItemMapper.toDtoWithBookings(item,
                                       lastBooking.size() != 1 ? null : ItemDtoWithBookings.BookingShort
                                               .toBookingShort(lastBooking.get(0)),
                                       nextBooking.size() != 1 ? null : ItemDtoWithBookings.BookingShort
                                               .toBookingShort(nextBooking.get(0)));
                           })
                           .sorted(Comparator.comparing(ItemDtoWithBookings::getId))
                           .collect(Collectors.toList());
    }
}
