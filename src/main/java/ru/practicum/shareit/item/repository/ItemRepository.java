package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс-репозиторий для вещей
 */
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryWithBookings {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    @Query("SELECT new Item(it.id, it.owner, it.name, it.description, it.available) " +
            "FROM Item AS it " +
            "WHERE it.available=true " +
            "AND (UPPER(it.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "     OR UPPER(it.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailableBy(@Param(value = "text") String text, Pageable pageable);
}
