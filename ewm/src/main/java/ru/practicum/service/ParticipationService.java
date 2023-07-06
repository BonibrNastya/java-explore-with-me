package ru.practicum.service;

import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getAll(Long userId);

    List<ParticipationRequestDto> getByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancel(Long userId, Long requestId);
}
