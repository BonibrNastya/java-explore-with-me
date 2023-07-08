package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Participation> findAllByRequesterId(Long userId);

    List<Participation> findAllByEventId(Long eventId);
}
