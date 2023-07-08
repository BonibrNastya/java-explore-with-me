package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.exception.BadRequestException;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    @Transactional
    public void createHit(HitDto hitDto) {
        hitRepository.save(HitMapper.toHit(hitDto));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validDate(start, end);
        if (unique) {
            if (!uris.isEmpty()) {
                return hitRepository.getUniqueStats(start, end, uris);
            } else return hitRepository.getUniqueStatsWithoutUris(start, end);
        } else {
            if (!uris.isEmpty()) {
                return hitRepository.getNotUniqueStats(start, end, uris);
            } else return hitRepository.getNotUniqueStatsWithoutUris(start, end);
        }
    }

    private void validDate(LocalDateTime start, LocalDateTime end) {
        if (isNull(start) || isNull(end) || end.isBefore(start)) {
            throw new BadRequestException("Дата конца не может быть раньше даты начала.");
        }
    }
}
