package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация бизнес-логики пользователей
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto findById(Long userId) {
        User user = findByIdOrThrow(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = findByIdOrThrow(userId);
        return UserMapper.toDto(updateRequiredFields(user, userDto));
    }

    private User updateRequiredFields(User user, UserDto userDto) {
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return user;
    }

    private User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id=%d is not found", userId)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
