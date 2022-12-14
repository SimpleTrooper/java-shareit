package ru.practicum.shareit.user;

/**
 * Маппер User - UserDto
 */
public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                   .id(userDto.getId())
                   .name(userDto.getName())
                   .email(userDto.getEmail())
                   .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                      .id(user.getId())
                      .name(user.getName())
                      .email(user.getEmail())
                      .build();
    }
}
