package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class CompilMapper {
    public static Compilation toCompil(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toCompilDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents() != null ? compilation.getEvents().stream()
                        .map(EventMapper::toShortEventDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public static Compilation toCompil(UpdateCompilationRequest request, Compilation oldCompil) {
        return Compilation.builder()
                .id(oldCompil.getId())
                .pinned(nonNull(request.getPinned()) ? request.getPinned() : oldCompil.getPinned())
                .title(nonNull(request.getTitle()) ? request.getTitle() : oldCompil.getTitle())
                .events(oldCompil.getEvents())
                .build();
    }
}
