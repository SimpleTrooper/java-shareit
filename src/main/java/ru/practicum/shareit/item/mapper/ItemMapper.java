package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер Item - ItemDto
 */
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        List<Comment> comments = itemDto.getComments().stream()
                                        .map(ItemDto.ItemComment::toComment)
                                        .collect(Collectors.toList());
        return Item.builder()
                   .id(itemDto.getId())
                   .name(itemDto.getName())
                   .description(itemDto.getDescription())
                   .available(itemDto.getAvailable())
                   .comments(comments)
                   .build();
    }

    public static ItemDto toDto(Item item) {
        List<ItemDto.ItemComment> comments = item.getComments().stream()
                                                 .map(ItemDto.ItemComment::toItemComment)
                                                 .collect(Collectors.toList());
        return ItemDto.builder()
                      .id(item.getId())
                      .name(item.getName())
                      .description(item.getDescription())
                      .available(item.getAvailable())
                      .comments(comments)
                      .build();
    }

    public static ItemDtoWithBookings toDtoWithBookings(Item item, ItemDtoWithBookings.BookingShort lastBooking,
                                                        ItemDtoWithBookings.BookingShort nextBooking) {
        List<ItemDto.ItemComment> comments = item.getComments().stream()
                                                 .map(ItemDto.ItemComment::toItemComment)
                                                 .collect(Collectors.toList());
        return ItemDtoWithBookings.builderWithBookings()
                                  .id(item.getId())
                                  .name(item.getName())
                                  .description(item.getDescription())
                                  .available(item.getAvailable())
                                  .comments(comments)
                                  .lastBooking(lastBooking)
                                  .nextBooking(nextBooking)
                                  .build();
    }
}
