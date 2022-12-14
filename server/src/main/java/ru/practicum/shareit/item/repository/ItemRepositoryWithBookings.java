package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

/**
 * Кастомный интерфейс репозитория - добавление полей Booking в ItemDTO
 */
public interface ItemRepositoryWithBookings {
    List<ItemDtoWithBookings> findAllByOwnerIdWithBookings(Long ownerId, Pageable pageable);
}
