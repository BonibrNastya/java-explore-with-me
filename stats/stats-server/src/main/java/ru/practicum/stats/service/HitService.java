package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void createHit(HitDto hitDto);

    List<Stats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
