package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.base.validation.groups.OnCreate;
import ru.practicum.shareit.base.validation.groups.OnUpdate;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер пользователей
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Request to get user with id={}", userId);
        UserDto returnedUser = userService.findById(userId);
        if (returnedUser != null) {
            log.info("Successfully returned user with id={}", userId);
        }
        return returnedUser;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Request to get all users");
        List<UserDto> users = userService.findAll();
        log.info("Successfully get all users");
        return users;
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto add(@Valid @RequestBody UserDto user) {
        log.info("Request to add new user: {}", user);
        UserDto returnedUser = userService.add(user);
        if (returnedUser != null) {
            log.info("Successfully added new user: {}", user);
        }
        return returnedUser;
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto user) {
        log.info("Request to update user: {}", user);
        UserDto returnedUser = userService.update(userId, user);
        if (returnedUser != null) {
            log.info("Successfully updated user with id={}", userId);
        }
        return returnedUser;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Request to delete user with id={}", userId);
        userService.delete(userId);
    }
}
