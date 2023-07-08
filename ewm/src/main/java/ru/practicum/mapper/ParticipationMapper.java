package ru.practicum.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.enums.Status;
import ru.practicum.model.Event;
import ru.practicum.model.Participation;
import ru.practicum.model.User;

import java.time.LocalDateTime;

public class ParticipationMapper {
    public static Participation toParticipation(User requester, Event event) {
        return Participation.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
    }

    public static ParticipationRequestDto toParticipationDto(Participation participation) {
        return ParticipationRequestDto.builder()
                .id(participation.getId())
                .created(participation.getCreated())
                .event(participation.getEvent().getId())
                .requester(participation.getRequester().getId())
                .status(participation.getStatus())
                .build();
    }
}
