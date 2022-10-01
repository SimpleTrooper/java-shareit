package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                      .id(commentDto.getId())
                      .text(commentDto.getText())
                      .created(commentDto.getCreated())
                      .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                         .id(comment.getId())
                         .authorName(comment.getAuthor().getName())
                         .text(comment.getText())
                         .created(comment.getCreated())
                         .build();
    }
}
