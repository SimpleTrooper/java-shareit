package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.base.groups.OnCreate;

import javax.validation.Valid;

/**
 * Контроллер пользователей
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("Request to get user with id={}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Request to get all users");
        return userClient.getUsers();
    }

    @PostMapping
    @Validated(value = OnCreate.class)
    public ResponseEntity<Object> add(@RequestBody @Valid UserDto user) {
        log.info("Request to add new user: {}", user);
        return userClient.add(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody @Valid UserDto user) {
        log.info("Request to update user: {}", user);
        return userClient.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Request to delete user with id={}", userId);
        return userClient.delete(userId);
    }
}
