package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void createHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
