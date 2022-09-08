package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Репозиторий в памяти для хранения пользователей
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new LinkedHashMap<>();
    private final Set<String> uniqueEmails = new LinkedHashSet<>();
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
        User updatedUser = users.computeIfPresent(user.getId(), (userId, oldUser) -> {
            String newEmail = user.getEmail();
            if (newEmail != null && !newEmail.equals(oldUser.getEmail()) && uniqueEmails.contains(newEmail)) {
                throw new DuplicateEmailException("Email is already existing");
            }
            if (newEmail != null && newEmail.length() != 0) {
                uniqueEmails.remove(oldUser.getEmail());
                uniqueEmails.add(newEmail);
                oldUser.setEmail(newEmail);
            }
            if (user.getName() != null && user.getName().length() != 0) {
                oldUser.setName(user.getName());
            }
            return oldUser;
        });
        if (updatedUser == null) {
            throw new UserNotFoundException(String.format("User with id=%d is not found", user.getId()));
        }
        return new User(updatedUser);
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
