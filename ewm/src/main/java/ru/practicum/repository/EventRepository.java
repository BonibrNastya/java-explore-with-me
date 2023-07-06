package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.dto.enums.State;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByCategory_Id(Long catId);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAllByInitiator_Id(Long userId, PageRequest page);

    Optional<Event> findByIdAndState(Long eventId, State state);
}
