package ru.practicum.mapper;

import ru.practicum.dto.*;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.enums.StateUser;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

public class EventMapper {
    public static EventShortDto toShortEventDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static Event toEventFromNew(NewEventDto eventDto, User user, Category category) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .initiator(user)
                .lat(eventDto.getLocation().getLat())
                .lon(eventDto.getLocation().getLon())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(State.PENDING)
                .title(eventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFull(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortUserDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public static Event toEventFromUpdateUser(Event ev, UpdateEventUserRequest req) {
        State state = ev.getState();
        StateUser stateUser = req.getStateAction();
        if (nonNull(stateUser)) {
            if (stateUser.equals(StateUser.CANCEL_REVIEW)) {
                state = State.CANCELED;
            } else {
                state = State.PENDING;
            }
        }
        return Event.builder()
                .id(ev.getId())
                .annotation(nonNull(req.getAnnotation()) ? req.getAnnotation() : ev.getAnnotation())
                .category(ev.getCategory())
                .confirmedRequests(ev.getConfirmedRequests())
                .createdOn(nonNull(req.getEventDate()) ? req.getEventDate() : ev.getEventDate())
                .description(nonNull(req.getDescription()) ? req.getDescription() : ev.getDescription())
                .eventDate(nonNull(req.getEventDate()) ? req.getEventDate() : ev.getEventDate())
                .initiator(ev.getInitiator())
                .lon(nonNull(req.getLocation()) ? req.getLocation().getLon() : ev.getLon())
                .lat(nonNull(req.getLocation()) ? req.getLocation().getLat() : ev.getLat())
                .paid(nonNull(req.getPaid()) ? req.getPaid() : ev.getPaid())
                .participantLimit(nonNull(req.getParticipantLimit()) ? req.getParticipantLimit() : ev.getParticipantLimit())
                .publishedOn(ev.getPublishedOn())
                .requestModeration(ev.getRequestModeration())
                .state(state)
                .title(nonNull(req.getTitle()) ? req.getTitle() : ev.getTitle())
                .build();
    }

    public static Event toEventFromUpdateAdmin(Event ev, UpdateEventAdminRequest req) {
        return Event.builder()
                .id(ev.getId())
                .annotation(nonNull(req.getAnnotation()) ? req.getAnnotation() : ev.getAnnotation())
                .category(ev.getCategory())
                .confirmedRequests(ev.getConfirmedRequests())
                .createdOn(nonNull(req.getEventDate()) ? req.getEventDate() : ev.getCreatedOn())
                .description(nonNull(req.getDescription()) ? req.getDescription() : ev.getDescription())
                .eventDate(nonNull(req.getEventDate()) ? req.getEventDate() : ev.getEventDate())
                .initiator(ev.getInitiator())
                .lon(nonNull(req.getLocation()) ? req.getLocation().getLon() : ev.getLon())
                .lat(nonNull(req.getLocation()) ? req.getLocation().getLat() : ev.getLat())
                .paid(nonNull(req.getPaid()) ? req.getPaid() : ev.getPaid())
                .participantLimit(nonNull(req.getParticipantLimit()) ? req.getParticipantLimit()
                        : ev.getParticipantLimit())
                .publishedOn(ev.getPublishedOn())
                .requestModeration(ev.getRequestModeration())
                .state(ev.getState())
                .title(nonNull(req.getTitle()) ? req.getTitle() : ev.getTitle())
                .build();
    }
}
