package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * Контроллер запросов на вещи
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    /**
     * Получение всех запросов пользователя
     * @param userId id пользователя
     * @return список запросов
     */
    @GetMapping
    public List<ItemRequestDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to get all by userId={}", userId);
        List<ItemRequestDto> requests = itemRequestService.findByUserId(userId);
        log.info("Successfully returned all by userId={}", userId);
        return requests;
    }

    /**
     * Получение всех запросов на вещи, кроме запросов пользователя. Пагинация от индекса from до размера size
     * @param userId id пользователя, запросы которого исключаются из поиска
     * @param from начальный индекс
     * @param size размер страницы
     * @return список найденных запросов на вещи
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getPageSortedByDate(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request to get {} requests from index={}", size, from);
        PaginationRequest paginationRequest = new PaginationRequest(from, size);
        List<ItemRequestDto> requests = itemRequestService.findPageSortedByDate(userId, paginationRequest);
        log.info("Successfully returned {} requests from index={}", size, from);
        return requests;
    }

    /**
     * Просмотр информации о запросе на вещь по id запроса
     * @param userId id аутентифицированного пользователя
     * @param requestId id запроса на вещь
     * @return Dto запроса
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long requestId) {
        log.info("Request to get item request by id={}", requestId);
        ItemRequestDto request = itemRequestService.findById(userId, requestId);
        if (request != null) {
            log.info("Successfully returned item request by id={}", requestId);
        }
        return request;
    }

    /**
     * Создание нового запроса
     * @param userId id пользователя, создающего запрос
     * @param itemRequestDto Dto запроса
     * @return Dto созданного запроса
     */
    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request to add item request from userId={}", userId);
        ItemRequestDto request = itemRequestService.add(userId, itemRequestDto);
        if (request != null) {
            log.info("Successfully added item request from id={}", userId);
        }
        return request;
    }
}
