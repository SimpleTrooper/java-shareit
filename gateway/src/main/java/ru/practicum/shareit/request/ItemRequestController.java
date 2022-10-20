package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
 * Контроллер запросов на вещи
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    /**
     * Получение всех запросов пользователя
     * @param userId id пользователя
     * @return список запросов
     */
    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to get all by userId={}", userId);
        return itemRequestClient.findByUserId(userId);
    }

    /**
     * Получение всех запросов на вещи, кроме запросов пользователя. Пагинация от индекса from до размера size
     * @param userId id пользователя, запросы которого исключаются из поиска
     * @param from начальный индекс
     * @param size размер страницы
     * @return список найденных запросов на вещи
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getPageSortedByDate(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get {} requests from index={}", size, from);
        return itemRequestClient.findPageSortedByDate(userId, from, size);
    }

    /**
     * Просмотр информации о запросе на вещь по id запроса
     * @param userId id аутентифицированного пользователя
     * @param requestId id запроса на вещь
     * @return Dto запроса
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.info("Request to get item request by id={}", requestId);
        return itemRequestClient.findById(userId, requestId);
    }

    /**
     * Создание нового запроса
     * @param userId id пользователя, создающего запрос
     * @param itemRequestDto Dto запроса
     * @return Dto созданного запроса
     */
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request to add item request from userId={}", userId);
        return itemRequestClient.add(userId, itemRequestDto);
    }
}
