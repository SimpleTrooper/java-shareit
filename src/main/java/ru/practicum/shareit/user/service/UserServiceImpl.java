package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация бизнес-логики пользователей
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(userRepository.getById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.add(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User oldUser = userRepository.getById(userId);
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        return UserMapper.toDto(userRepository.update(oldUser));
    }

    @Override
    public boolean delete(Long userId) {
        return userRepository.delete(userId);
    }
}
