package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Репозиторий бронирований
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start <= ?2 AND b.end >= ?2 AND b.booker.id = ?1")
    List<Booking> findAllByBookerWhereTimeIsInside(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemIdIn(Collection<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItemIdInAndStatus(Collection<Long> itemIds, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemIdInAndStartAfter(Collection<Long> itemIds, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemIdInAndEndBefore(Collection<Long> itemIds, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start <= ?2 AND b.end >= ?2 AND b.item.id IN (?1)")
    List<Booking> findAllByItemIdInWhereTimeIsInside(Collection<Long> itemIds, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start < current_timestamp AND b.item.id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastByItemId(Long itemId, Pageable page);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > current_timestamp AND b.item.id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC ")
    List<Booking> findFutureByItemId(Long itemId, Pageable page);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.end < current_timestamp  AND b.status = 'APPROVED' AND b.booker.id = ?1 AND b.item.id = ?2")
    List<Booking> findPastApprovedByBookerAndItem(Long bookerId, Long itemId, Pageable page);
}
