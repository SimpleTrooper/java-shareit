package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.base.validation.groups.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDto {
    @Null(groups = OnCreate.class)
    private Long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @Email
    @NotBlank(groups = OnCreate.class)
    private String email;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
