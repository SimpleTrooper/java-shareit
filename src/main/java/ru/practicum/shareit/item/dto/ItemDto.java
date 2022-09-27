package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.base.validation.groups.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO вещи
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    @Null(groups = OnCreate.class)
    private Long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();

    @Override
    public String toString() {
        return "ItemDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                '}';
    }
}
