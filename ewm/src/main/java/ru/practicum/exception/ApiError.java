package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError extends RuntimeException {
    private List<String> errors = new ArrayList<>();
    private String message;
    private String reason;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
