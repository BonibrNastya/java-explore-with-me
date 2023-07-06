package ru.practicum.controller.publicController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoriesPublic {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return categoryService.getAll(PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getById(@PathVariable Long categoryId) {
        return categoryService.getById(categoryId);
    }
}
