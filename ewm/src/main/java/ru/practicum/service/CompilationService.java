package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto compilationDto);

    List<CompilationDto> getAll(Boolean pinned, PageRequest page);

    CompilationDto getById(Long compilationId);

    CompilationDto update(Long compilationId, UpdateCompilationRequest request);

    void delete(Long compilationId);
}
