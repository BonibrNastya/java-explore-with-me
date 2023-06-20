package ru.practicum.stats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class StatsController {
    private final HitService hitService;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsController(HitService hitService) {
        this.hitService = hitService;
    }

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody HitDto hitDto) {
        hitService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<Stats> getStats(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(value = "uris", required = false, defaultValue = "") List<String> uris,
                                @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean uniq) {
        LocalDateTime updStart = LocalDateTime.parse(start, FORMAT);
        LocalDateTime updEnd = LocalDateTime.parse(end, FORMAT);
        return hitService.getStats(updStart, updEnd, uris, uniq);
    }
}
