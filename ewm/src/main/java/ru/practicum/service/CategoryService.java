package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto categoryDto);

    List<CategoryDto> getAll(PageRequest page);

    CategoryDto getById(Long categoryId);

    CategoryDto update(Long categoryId, NewCategoryDto categoryDto);

    void delete(Long categoryId);
}
