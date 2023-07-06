package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody HitDto hitDto) {
        hitService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(required = false)
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(value = "uris", required = false, defaultValue = "") List<String> uris,
                                   @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean uniq) {
        return hitService.getStats(start, end, uris, uniq);
    }
}
