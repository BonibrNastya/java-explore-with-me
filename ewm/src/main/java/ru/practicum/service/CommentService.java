package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CommentByUserDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentFromRequestDto;
import ru.practicum.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto create(CommentFromRequestDto comment, Long eventId, Long userId);

    List<CommentDto> getAllByEvent(Long eventId, PageRequest page);

    List<CommentByUserDto> getAllByUser(Long userId, PageRequest page);

    CommentDto getById(Long commentId, Long userId);

    CommentUpdateDto update(CommentFromRequestDto comment, Long userId, Long commentId);

    void delete(Long commentId, Long userId);
}
