package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.base.validation.groups.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * DTO комментария
 */
@Getter
@Setter
@Builder
public class CommentDto {
    @Null(groups = OnCreate.class)
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
