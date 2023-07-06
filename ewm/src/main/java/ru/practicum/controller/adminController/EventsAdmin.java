package ru.practicum.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.enums.State;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@Validated
@RequiredArgsConstructor
public class EventsAdmin {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(value = "users[]", required = false) List<Long> users,
                                     @RequestParam(required = false) List<State> states,
                                     @RequestParam(value = "categories[]", required = false) List<Long> categories,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(required = false, defaultValue = "0")
                                     @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10")
                                     @Positive Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("eventDate").descending());
        List<EventFullDto> events = eventService.getAllAdmin(users, states, categories, rangeStart, rangeEnd, page);
        log.info(events.toString());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequest request) {
        return eventService.updateAdmin(eventId, request);
    }
}
