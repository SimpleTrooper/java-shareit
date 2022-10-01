package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.base.validation.groups.OnCreate;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
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
    private List<ItemComment> comments = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ItemComment {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;

        public static ItemComment toItemComment(Comment comment) {
            return ItemComment.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .authorName(comment.getAuthor().getName())
                    .created(comment.getCreated())
                    .build();
        }

        public static Comment toComment(ItemComment comment) {
            return Comment.builder()
                              .id(comment.getId())
                              .text(comment.getText())
                              .created(comment.getCreated())
                              .build();
        }
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                '}';
    }
}
