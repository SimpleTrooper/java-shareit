package ru.practicum.shareit.base.pagination;

import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Класс для строки запроса на пагинацию
 */
@EqualsAndHashCode
public final class PaginationRequest {
    private final Integer from;
    private final Integer size;

    public PaginationRequest(Integer from, Integer size) {
        this.from = from;
        this.size = size;
    }

    public Pageable makePaginationByFieldDesc(String field) {
        Sort sortingByDateCreatedAsc = Sort.by(Sort.Direction.DESC, field);
        return OffsetBasedPageRequest.of(from, size, sortingByDateCreatedAsc);
    }

    public Pageable makePaginationByFieldAsc(String field) {
        Sort sortingByDateCreatedAsc = Sort.by(Sort.Direction.ASC, field);
        return OffsetBasedPageRequest.of(from, size, sortingByDateCreatedAsc);
    }
}
