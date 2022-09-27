package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Класс запроса вещи для работы с БД
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "requester_id")
    private long requesterId;
    @Column(name = "description")
    private String itemDescription;
    @Column(name = "created")
    private LocalDateTime created;

    public ItemRequest(ItemRequest request) {
        this.id = request.id;
        this.requesterId = request.requesterId;
        this.itemDescription = request.itemDescription;
        this.created = request.created;
    }
}
