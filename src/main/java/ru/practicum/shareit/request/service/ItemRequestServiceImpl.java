package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.base.pagination.PaginationRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация бизнес логики запросов на вещи
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestDto> findByUserId(Long userId) {
        getUserByIdOrThrow(userId);
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                                        .map(request -> ItemRequestMapper.toItemRequestDto(request,
                                            itemRepository.findAllByRequestId(request.getId())))
                                        .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findPageSortedByDate(Long userId, PaginationRequest paginationRequest) {
        getUserByIdOrThrow(userId);
        return itemRequestRepository.findAllByRequesterIdNot(userId,
                                            paginationRequest.makePaginationByFieldDesc("created"))
                                        .stream()
                                        .map(request -> ItemRequestMapper.toItemRequestDto(request,
                                            itemRepository.findAllByRequestId(request.getId())))
                                        .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        getUserByIdOrThrow(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            throw new ItemRequestNotFoundException(String.format("Item request with id=%d is not found", requestId));
        }
        return ItemRequestMapper.toItemRequestDto(itemRequest.get(), itemRepository.findAllByRequestId(requestId));
    }

    @Override
    @Transactional
    public ItemRequestDto add(Long requesterId, ItemRequestDto itemRequestDto) {
        User requester = getUserByIdOrThrow(requesterId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requester, itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id=%d is not found", userId)));
    }
}
