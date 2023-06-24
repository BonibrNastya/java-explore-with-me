package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.exception.DateTimeException;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    public List<Stats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
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
        if (end.isBefore(start)) {
            throw new DateTimeException("Дата конца не может быть перед датой начала.");
        }
    }
}
