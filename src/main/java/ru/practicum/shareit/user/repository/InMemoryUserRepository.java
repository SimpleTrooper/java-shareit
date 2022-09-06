package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Репозиторий в памяти для хранения пользователей
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmails = new HashSet<>();
    private long nextId = 0;

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("User with id=%d is not found", userId));
        }
        return new User(user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        if (uniqueEmails.contains(user.getEmail())) {
            throw new DuplicateEmailException("Email is already existing");
        }
        User newUser = new User(user);
        nextId++;
        newUser.setId(nextId);
        users.put(nextId, newUser);
        uniqueEmails.add(user.getEmail());
        return new User(newUser);
    }

    @Override
    public User update(User user) {
        User oldUser = getById(user.getId());
        if (oldUser == null) {
            throw new UserNotFoundException(String.format("User with id=%d is not found", user.getId()));
        }
        uniqueEmails.remove(oldUser.getEmail());
        if (uniqueEmails.contains(user.getEmail())) {
            uniqueEmails.add(oldUser.getEmail());
            throw new DuplicateEmailException("Email is already existing");
        }
        uniqueEmails.add(user.getEmail());
        users.put(user.getId(), new User(user));
        return user;
    }

    @Override
    public boolean delete(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            uniqueEmails.remove(user.getEmail());
        }
        return users.remove(userId) != null;
    }
}
