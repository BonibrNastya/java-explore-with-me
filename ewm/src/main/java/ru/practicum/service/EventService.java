package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.*;
import ru.practicum.dto.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllPrivate(Long userId, PageRequest page);

    EventFullDto getByIdPrivate(Long userId, Long eventId);

    List<EventFullDto> getAllAdmin(List<Long> users, List<State> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page);

    List<EventShortDto> getAllPublic(String text, Boolean paid, Boolean onlyAvailable, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page,
                                     HttpServletRequest httpRequest, String app, String sort);

    EventFullDto getByIdPublic(Long eventId, HttpServletRequest httpRequest, String app);

    EventFullDto updatePrivate(Long userId, Long eventId, UpdateEventUserRequest request);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest request);
}
