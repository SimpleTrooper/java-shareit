package ru.practicum.shareit.item;

import ru.practicum.shareit.base.validation.groups.OnUpdate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.base.validation.groups.OnCreate;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер вещей
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Добавление новой вещи пользователем
     *
     * @param userId  id пользователя
     * @param itemDto DTO вещи
     * @return DTO созданной вещи
     */
    @PostMapping
    @Validated(OnCreate.class)
    public ItemDto add(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Request to add new item with ownerId={} and item: {}", userId, itemDto);
        ItemDto returnedItem = itemService.add(userId, itemDto);
        if (returnedItem != null) {
            log.info("Successfully added new item with ownerId={}", userId);
        }
        return returnedItem;
    }

    /**
     * Обновление вещи пользователем
     *
     * @param userId  id пользователя
     * @param itemId  id вещи
     * @param itemDto DTO вещи
     * @return DTO обновленной вещи
     */
    @PatchMapping("/{itemId}")
    @Validated(OnUpdate.class)
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Request to update item with ownerId={} and itemId={}", userId, itemId);
        ItemDto returnedItem = itemService.update(userId, itemId, itemDto);
        if (returnedItem != null) {
            log.info("Successfully updated item with ownerId={} and itemId={}", userId, itemId);
        }
        return returnedItem;
    }

    /**
     * Получение вещи по id
     *
     * @param itemId id вещи
     * @return DTO вещи
     */
    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(name = "X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Request to get item with itemId={}", itemId);
        ItemDto returnedItem = itemService.findById(userId, itemId);
        if (returnedItem != null) {
            log.info("Successfully get item with itemId={}", itemId);
        }
        return returnedItem;
    }

    /**
     * Получение всех вещей пользователя
     *
     * @param ownerId id пользователя
     * @return Список DTO вещей
     */
    @GetMapping
    public List<ItemDtoWithBookings> getAllByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Request to get all items by ownerId={}", ownerId);
        List<ItemDtoWithBookings> items = itemService.findAllByOwnerId(ownerId);
        log.info("Successfully get all items by ownerId={}", ownerId);
        return items;
    }

    /**
     * Поиск доступных вещей по названию и описанию
     *
     * @param text строка поиска
     * @return Список DTO вещей
     */
    @GetMapping("/search")
    public List<ItemDto> searchBy(@RequestParam String text) {
        log.info("Request to get all available items by text={}", text);
        List<ItemDto> items = itemService.searchAvailableBy(text);
        log.info("Successfully got all available items by text={}", text);
        return items;
    }

    /**
     * Добавление комментария к вещи
     *
     * @param userId     id пользователя
     * @param itemId     id вещи
     * @param commentDto DTO комментария
     * @return
     */
    @PostMapping("/{itemId}/comment")
    @Validated(OnCreate.class)
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Request to add comment to item with id = {} from user with id = {}", itemId, userId);
        CommentDto result = itemService.addComment(userId, itemId, commentDto);
        log.info("Successfully added comment to item with id = {} from user with id = {}", itemId, userId);
        return result;
    }
}
