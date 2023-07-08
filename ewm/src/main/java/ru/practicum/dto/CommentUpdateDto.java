package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentUpdateDto {
    private Long id;
    private String text;
    private String oldText;
    private Long eventId;
    private String annotation;
    private String authorName;
    private LocalDateTime created;
}
