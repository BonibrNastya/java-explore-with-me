package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Hit;

public class HitMapper {
    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
