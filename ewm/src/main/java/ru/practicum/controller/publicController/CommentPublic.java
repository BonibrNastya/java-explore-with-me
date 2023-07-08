package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentPublic {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllByEvent(@RequestParam Long eventId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllByEvent(eventId, PageRequest.of(from > 0 ? from / size : 0, size));
    }
}
