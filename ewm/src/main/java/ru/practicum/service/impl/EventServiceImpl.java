package ru.practicum.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.enums.StateAdmin;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.stats.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.practicum.dto.enums.State.PUBLISHED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        isAfterHours(eventDto.getEventDate(), 2);
        User user = getUserOrException(userId);
        Category category = getCategoryOrException(eventDto.getCategory());
        Event event = EventMapper.toEventFromNew(eventDto, user, category);
        return EventMapper.toEventFull(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllPrivate(Long userId, PageRequest page) {
        getUserOrException(userId);
        return eventRepository.findAllByInitiator_Id(userId, page).stream()
                .map(EventMapper::toShortEventDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdPrivate(Long userId, Long eventId) {
        getUserOrException(userId);
        return EventMapper.toEventFull(getEventOrException(eventId));
    }

    @Override
    public List<EventFullDto> getAllAdmin(List<Long> users, List<State> states, List<Long> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page) {
        final BooleanExpression condition = getAllForAdmin(users, states, categories, rangeStart, rangeEnd);
        return eventRepository.findAll(condition, page).stream().map(EventMapper::toEventFull)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllPublic(String text, Boolean paid, Boolean onlyAvailable, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page,
                                            HttpServletRequest httpRequest, String app, String sort) {
        if (nonNull(rangeEnd) && rangeEnd.isBefore(rangeStart)) {
            throw new NumberFormatException("Дата конца не может быть раньше даты начала.");
        }
        statsClient.postHits(httpRequest, app);
        BooleanExpression conditions = getAllForPublic(text, paid, onlyAvailable, categories, rangeStart, rangeEnd);
        List<EventShortDto> events = eventRepository.findAll(conditions, page).stream()
                .map(EventMapper::toShortEventDto).collect(Collectors.toList());
        List<StatsDto> statsDtos = statsClient.getStats(LocalDateTime.now().minusYears(1), LocalDateTime.now(),
                createUris(events), true);
        return createStats(events, sort, statsDtos);
    }

    @Override
    public EventFullDto getByIdPublic(Long eventId, HttpServletRequest httpRequest, String app) {
        statsClient.postHits(httpRequest, app);
        Event event = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        EventFullDto eventDto = EventMapper.toEventFull(event);
        StatsDto statsDto = statsClient.getStats(LocalDateTime.now().minusDays(1000), LocalDateTime.now(),
                List.of("/events/" + eventDto.getId()), true).stream().findAny().get();
        eventDto.setViews(statsDto.getHits());
        return eventDto;
    }

    @Override
    @Transactional
    public EventFullDto updatePrivate(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = getEventOrException(eventId);
        isAfterHours(event.getEventDate(), 2);
        if (event.getState().equals(PUBLISHED)) {
            throw new IllegalStateException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации.");
        }
        getUserOrException(userId);
        if (nonNull(request.getCategory())) {
            Category requestCategory = getCategoryOrException(request.getCategory());
            event.setCategory(requestCategory);
        }
        Event update = EventMapper.toEventFromUpdateUser(event, request);
        return EventMapper.toEventFull(eventRepository.save(update));
    }

    @Override
    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = getEventOrException(eventId);
        if (event.getState().equals(PUBLISHED)) {
            throw new IllegalStateException("Событие уже опубликовано.");
        }
        isAfterHours(event.getEventDate(), 1);
        if (nonNull(request.getCategory())) {
            Category requestCategory = getCategoryOrException(request.getCategory());
            event.setCategory(requestCategory);
        }
        Event update = EventMapper.toEventFromUpdateAdmin(event, request);
        if (nonNull(request.getStateAction())) {
            if (request.getStateAction().equals(StateAdmin.PUBLISH_EVENT) && !event.getState().equals(State.PENDING)) {
                throw new IllegalStateException("Событие можно публиковать, " +
                        "только если оно в состоянии ожидания публикации");
            } else if (request.getStateAction().equals(StateAdmin.PUBLISH_EVENT)) {
                update.setState(PUBLISHED);
                update.setPublishedOn(LocalDateTime.now());
            } else {
                update.setState(State.REJECT);
            }
        }
        return EventMapper.toEventFull(eventRepository.save(update));
    }

    private Event getEventOrException(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено."));
    }

    private User getUserOrException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));
    }

    private Category getCategoryOrException(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория не найдена."));
    }

    private static void isAfterHours(LocalDateTime localDateTime, int time) {
        if (localDateTime.isBefore(LocalDateTime.now().plusHours(time))) {
            throw new IllegalStateException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через " + time + "час(а) от текущего момента.");
        }
    }

    private BooleanExpression getAllForAdmin(List<Long> users, List<State> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanExpression conditions;
        if (isNull(users)) {
            conditions = qEvent.initiator.id.notIn(new ArrayList<>());
        } else {
            conditions = qEvent.initiator.id.in(users);
        }
        if (nonNull(categories)) {
            conditions.and(qEvent.category.id.in(categories));
        }
        if (nonNull(states)) {
            conditions.and(qEvent.state.in(states));
        }
        if (nonNull(rangeStart) && nonNull(rangeEnd)) {
            conditions.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        }
        return conditions;
    }

    private BooleanExpression getAllForPublic(String text, Boolean paid, Boolean onlyAvailable, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        QEvent qEvent = QEvent.event;
        BooleanExpression conditions = qEvent.state.eq(PUBLISHED);
        if (onlyAvailable) {
            conditions.and(qEvent.participantLimit.ne(qEvent.confirmedRequests));
        }
        if (nonNull(categories)) {
            conditions.and(qEvent.category.id.in(categories));
        }
        if (nonNull(text)) {
            conditions.and(qEvent.annotation.containsIgnoreCase(text));
            conditions.and(qEvent.description.containsIgnoreCase(text));
            conditions.and(qEvent.title.containsIgnoreCase(text));
        }
        if (nonNull(paid)) {
            conditions.and(qEvent.paid.eq(paid));
        }
        if (nonNull(rangeEnd)) {
            conditions.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        } else {
            conditions.and(qEvent.eventDate.after(rangeStart));
        }
        return conditions;
    }

    private List<String> createUris(List<EventShortDto> eventDtos) {
        return eventDtos
                .stream()
                .map(r -> "/events/" + r.getId())
                .collect(Collectors.toList());
    }

    private Long inId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private List<EventShortDto> createStats(List<EventShortDto> event, String sort, List<StatsDto> stats) {
        Map<Long, Long> longMap = stats
                .stream().collect(Collectors.toMap((s -> inId(s.getUri())), (StatsDto::getHits)));
        event.forEach(e -> e.setViews(longMap.get(e.getId())));
        if (sort.equals("VIEWS")) {
            return event.stream().sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        return event;
    }
}