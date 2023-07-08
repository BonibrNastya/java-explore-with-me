package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationsPublic {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(defaultValue = "false") Boolean pinned,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getAll(pinned, PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @GetMapping("/{compilationId}")
    public CompilationDto getById(@PathVariable Long compilationId) {
        return compilationService.getById(compilationId);
    }
}
