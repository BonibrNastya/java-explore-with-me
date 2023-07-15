package ru.practicum.controller.privateController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentByUserDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentFromRequestDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivate {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long userId,
                             @RequestParam Long eventId,
                             @RequestBody @Valid CommentFromRequestDto newComment) {
        return commentService.create(newComment, eventId, userId);
    }

    @GetMapping
    public List<CommentByUserDto> getAllByUser(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllByUser(userId, PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.getById(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    public CommentUpdateDto update(@PathVariable Long userId,
                                   @PathVariable Long commentId,
                                   @RequestBody @Valid CommentFromRequestDto comment) {
        return commentService.update(comment, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.delete(commentId, userId);
    }
}
