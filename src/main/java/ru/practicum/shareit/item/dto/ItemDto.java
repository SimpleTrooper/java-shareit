package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
