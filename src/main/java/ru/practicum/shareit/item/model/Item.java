package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс вещи для работы с БД
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(name = "item_name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @Transient
    private ItemRequest request;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();

    public Item(Item item) {
        this.id = item.id;
        this.owner = new User(item.owner);
        this.name = item.name;
        this.description = item.description;
        this.available = item.available;
        this.request = item.request == null ? null : item.request;
    }

    public Item(long id, User owner, String name, String description, Boolean available) {
        this.id = id;
        this.owner = new User(owner);
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
