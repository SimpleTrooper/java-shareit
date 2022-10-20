package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Comment;

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
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Builder.Default
    private List<ItemComment> comments = new ArrayList<>();
    private Long requestId;

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
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
    }
}
