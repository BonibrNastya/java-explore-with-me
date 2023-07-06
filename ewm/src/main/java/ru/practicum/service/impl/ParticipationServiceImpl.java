package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.enums.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Participation;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.ParticipationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User requester = getUserOrException(userId);
        Event event = getEventOrException(eventId);
        if (participationRepository.existsByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new IllegalStateException("Нельзя добавить повторный запрос.");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new IllegalStateException("Инициатор события не может добавить запрос на участие в своём событии.");
        }
        if (!State.PUBLISHED.equals(event.getState())) {
            throw new IllegalStateException("Нельзя участвовать в неопубликованном событии.");
        }
        if (nonNull(event.getParticipantLimit()) && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new IllegalStateException("У события достигнут лимит запросов на участие.");
        }
        Participation participation = ParticipationMapper.toParticipation(requester, event);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            participation.setStatus(Status.CONFIRMED);
            if (isNull(event.getConfirmedRequests())) {
                event.setConfirmedRequests(1);
            } else {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
            eventRepository.save(event);
        }
        return ParticipationMapper.toParticipationDto(participationRepository.save(participation));
    }

    @Override
    public List<ParticipationRequestDto> getAll(Long userId) {
        getUserOrException(userId);
        return requestDtoList(participationRepository.findAllByRequester_Id(userId));
    }

    @Override
    public List<ParticipationRequestDto> getByEventId(Long userId, Long eventId) {
        getUserOrException(userId);
        getEventOrException(eventId);
        return requestDtoList(participationRepository.findAllByEvent_Id(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        getUserOrException(userId);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(
                () -> new NotFoundException("Событие не найдено."));
        if (!event.getRequestModeration() || isNull(event.getParticipantLimit())) {
            throw new IllegalStateException("Лимит заявок равен 0 или отключена пре-модерация заявок. " +
                    "Подтверждение заявок не требуется.");
        }
        List<Participation> participations = participationRepository.findAllById(request.getRequestIds());
        boolean isNotPending = participations.stream().anyMatch(p -> !p.getStatus().equals(Status.PENDING));
        if (isNotPending) {
            throw new IllegalStateException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }
        if (request.getStatus().equals(Status.CONFIRMED)) {
            return changeStatusToConfirmed(request.getRequestIds(), event);
        } else if (request.getStatus().equals(Status.REJECTED)) {
            participations.forEach(p -> p.setStatus(Status.REJECTED));
            participationRepository.saveAll(participations);
            List<ParticipationRequestDto> participationDtos = participations.stream()
                    .map(ParticipationMapper::toParticipationDto).collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(List.of(), participationDtos);
        } else {
            throw new IllegalStateException("Заявку можно либо подтвердить, либо отклонить.");
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        getUserOrException(userId);
        Participation participation = getParticipationOrException(requestId);
        participation.setStatus(Status.CANCELED);
        return ParticipationMapper.toParticipationDto(participationRepository.save(participation));
    }

    private EventRequestStatusUpdateResult changeStatusToConfirmed(List<Long> ids, Event event) {
        int requestCount = 0;
        List<Participation> confirmed;
        List<Participation> rejected;
        Integer limit = event.getParticipantLimit();
        Integer confirmedRequests = event.getConfirmedRequests();
        if (nonNull(limit) && limit.equals(confirmedRequests)) {
            throw new IllegalStateException("Нельзя подтвердить заявку, " +
                    "если уже достигнут лимит по заявкам на данное событие");
        }
        if (nonNull(confirmedRequests)) {
            requestCount = confirmedRequests;
        }
        if (isNull(limit) || ids.size() < (limit - requestCount)) {
            confirmed = participationRepository.findAllById(ids);
        } else {
            confirmed = participationRepository.findAllById(ids).stream()
                    .limit(limit - requestCount)
                    .collect(Collectors.toList());
        }
        for (Participation p : confirmed) {
            p.setStatus(Status.CONFIRMED);
            requestCount++;
        }
        List<Long> confirmedIds = confirmed.stream().map(Participation::getId).collect(Collectors.toList());
        List<Long> rejectedIds = new ArrayList<>(ids);
        rejectedIds.removeAll(confirmedIds);
        rejected = participationRepository.findAllById(rejectedIds);
        for (Participation p : rejected) {
            p.setStatus(Status.REJECTED);
        }
        confirmed.addAll(rejected);
        participationRepository.saveAll(confirmed);
        event.setConfirmedRequests(requestCount);
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmed.stream()
                .map(ParticipationMapper::toParticipationDto).collect(Collectors.toList()),
                rejected.stream().map(ParticipationMapper::toParticipationDto).collect(Collectors.toList()));
    }

    private User getUserOrException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));
    }

    private Event getEventOrException(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено."));
    }

    private Participation getParticipationOrException(Long participationId) {
        return participationRepository.findById(participationId).orElseThrow(
                () -> new NotFoundException("Запрос не найден."));
    }

    private List<ParticipationRequestDto> requestDtoList(List<Participation> participation) {
        return participation.stream().map(ParticipationMapper::toParticipationDto).collect(Collectors.toList());
    }
}
