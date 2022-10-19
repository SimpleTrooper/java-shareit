package ru.practicum.shareit.request.service;

import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Интерфейс бизнес логики запросов на вещи
 */
public interface ItemRequestService {
    /**
     * Все запросы пользователя с id=userId
     *
     * @param userId id пользователя
     * @return список запросов пользователя
     */
    List<ItemRequestDto> findByUserId(Long userId);

    /**
     * Возвращение size запросов начиная с from, отсортированных по дате создания - от новых к старым,
     * всех пользователей
     *
     * @param from индекс - от
     * @param size количество
     * @return список Dto запросов
     */
    List<ItemRequestDto> findPageSortedByDate(Long userId, PaginationRequest paginationRequest);

    /**
     * Возвращение запроса по id
     *
     * @param userId id аутентифицированного пользователя
     * @param requestId id запроса
     * @return Dto запроса
     */
    ItemRequestDto findById(Long userId, Long requestId);

    /**
     * Добавление нового запроса на вещь
     *
     * @param requesterId    id пользователя
     * @param itemRequestDto Dto запроса
     * @return Dto добавленного запроса
     */
    ItemRequestDto add(Long requesterId, ItemRequestDto itemRequestDto);
}
