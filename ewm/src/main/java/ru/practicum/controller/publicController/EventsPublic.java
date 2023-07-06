package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventsPublic {
    private final EventService eventService;
    private static final String APP = "ewm-main-service";

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size,
                                      HttpServletRequest request) {
        PageRequest page;
        if (sort.equals("EVENT_DATE")) {
            Sort sortedBy = Sort.by("eventDate").descending();
            page = PageRequest.of(from > 0 ? from / size : 0, size, sortedBy);
        } else {
            page = PageRequest.of(from > 0 ? from / size : 0, size);
        }
        return eventService.getAllPublic(text, paid, onlyAvailable, categories,
                rangeStart, rangeEnd, page, request, APP, sort);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable Long eventId,
                                HttpServletRequest request) {
        return eventService.getByIdPublic(eventId, request, APP);
    }
}
