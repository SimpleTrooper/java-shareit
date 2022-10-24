package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.base.groups.OnCreate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Контроллер вещей
 */
@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    /**
     * Добавление новой вещи пользователем
     *
     * @param userId  id пользователя
     * @param itemDto DTO вещи
     * @return DTO созданной вещи
     */
    @PostMapping
    @Validated(value = OnCreate.class)
    public ResponseEntity<Object> add(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        log.info("Request to add new item with ownerId={} and item: {}", userId, itemDto);
        return itemClient.add(userId, itemDto);
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
    public ResponseEntity<Object> update(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Request to update item with ownerId={} and itemId={}", userId, itemId);
        return itemClient.update(userId, itemId, itemDto);
    }

    /**
     * Получение вещи по id
     *
     * @param itemId id вещи
     * @return DTO вещи
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("Request to get item with itemId={}", itemId);
        return itemClient.getById(userId, itemId);
    }

    /**
     * Получение всех вещей пользователя
     *
     * @param ownerId id пользователя
     * @param from начальный индекс для пагинации
     * @param size размер страницы для пагинации
     * @return Список DTO вещей
     */
    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get all items by ownerId={}", ownerId);
        return itemClient.getByOwnerId(ownerId, from, size);
    }

    /**
     * Поиск доступных вещей по названию и описанию
     *
     * @param text строка поиска
     * @param from начальный индекс для пагинации
     * @param size размер страницы для пагинации
     * @return Список DTO вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchBy(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                           @RequestParam String text,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get all available items by text={}", text);
        return itemClient.searchBy(userId, text, from, size);
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
    public ResponseEntity<Object> addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Request to add comment to item with id = {} from user with id = {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
