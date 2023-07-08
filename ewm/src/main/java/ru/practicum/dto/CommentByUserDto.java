package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentByUserDto {
    private Long id;
    private String text;
    private Long eventId;
    private String annotation;
    private LocalDateTime created;
}
