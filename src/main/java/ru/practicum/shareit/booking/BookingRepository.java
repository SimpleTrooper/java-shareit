package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Репозиторий бронирований
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Booking AS b SET b.status = ?2 WHERE b.id = ?1")
    int updateStatusById(Long bookingId, BookingStatus status);

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start <= ?2 AND b.end >= ?2 AND b.booker.id = ?1")
    List<Booking> findAllByBookerWhereTimeIsInside(Long bookerId, LocalDateTime time);

    List<Booking> findAllByItemIdIn(Collection<Long> itemIds);

    List<Booking> findAllByItemIdInAndStatus(Collection<Long> itemIds, BookingStatus status);

    List<Booking> findAllByItemIdInAndStartAfter(Collection<Long> itemIds, LocalDateTime time);

    List<Booking> findAllByItemIdInAndEndBefore(Collection<Long> itemIds, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start <= ?2 AND b.end >= ?2 AND b.item.id IN (?1)")
    List<Booking> findAllByItemIdInWhereTimeIsInside(Collection<Long> itemIds, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start < current_timestamp AND b.item.id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC ")
    List<BookingShort> findPastByItemId(Long itemId, Pageable page);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > current_timestamp AND b.item.id = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC ")
    List<BookingShort> findFutureByItemId(Long itemId, Pageable page);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.end < current_timestamp  AND b.status = 'APPROVED' AND b.booker.id = ?1 AND b.item.id = ?2")
    List<Booking> findPastApprovedByBookerAndItem(Long bookerId, Long itemId, Pageable page);
}