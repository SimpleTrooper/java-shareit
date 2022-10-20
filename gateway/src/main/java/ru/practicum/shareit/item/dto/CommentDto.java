package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * DTO комментария
 */
@Getter
@Setter
public class CommentDto {
    @NotBlank
    private String text;
}
