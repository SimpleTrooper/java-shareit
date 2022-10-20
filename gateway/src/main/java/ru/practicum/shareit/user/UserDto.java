package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.base.groups.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO пользователя
 */
@Data
@Builder
public class UserDto {
    @NotBlank(groups = OnCreate.class)
    private String name;
    @Email
    @NotBlank(groups = OnCreate.class)
    private String email;
}
