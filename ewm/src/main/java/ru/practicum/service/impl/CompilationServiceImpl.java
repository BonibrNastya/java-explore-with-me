package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto compilationDto) {
        Compilation compilation = CompilMapper.toCompil(compilationDto);
        List<Long> ids = compilationDto.getEvents();
        Compilation update = setEventsToCompilation(compilation, ids);
        return CompilMapper.toCompilDto(update);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest page) {
        return compilationRepository.findAllByPinned(pinned, page)
                .stream().map(CompilMapper::toCompilDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compilationId) {
        return CompilMapper.toCompilDto(getCompilOrException(compilationId));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compilationId, UpdateCompilationRequest request) {
        Compilation old = getCompilOrException(compilationId);
        if (nonNull(request.getEvents())) {
            old.setEvents(eventRepository.findAllById(request.getEvents()));
        }
        Compilation update = CompilMapper.toCompil(request, old);
        return CompilMapper.toCompilDto(update);
    }

    @Override
    @Transactional
    public void delete(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Подборка не найдена.");
        }
        compilationRepository.deleteById(compilationId);

    }

    private Compilation getCompilOrException(Long compilId) {
        return compilationRepository.findById(compilId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена."));
    }

    private Compilation setEventsToCompilation(Compilation compilation, List<Long> ids) {
        if (nonNull(ids)) {
            compilation.setEvents(eventRepository.findAllById(ids));
        }
        return compilationRepository.save(compilation);
    }
}
