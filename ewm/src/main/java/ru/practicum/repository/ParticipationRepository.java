package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Boolean existsByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<Participation> findAllByRequester_Id(Long userId);

    List<Participation> findAllByEvent_Id(Long eventId);
}
