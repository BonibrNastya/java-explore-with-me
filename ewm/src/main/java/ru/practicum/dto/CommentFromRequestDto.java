package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CommentFromRequestDto {
    @NotEmpty
    @Size(min = 1, max = 280)
    private String text;
}
