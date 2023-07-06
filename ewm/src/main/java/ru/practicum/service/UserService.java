package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll(PageRequest page);

    List<UserDto> getByIds(List<Long> ids);

    void delete(Long userId);
}
