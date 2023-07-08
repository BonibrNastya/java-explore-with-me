package ru.practicum.stats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError badRequestHandler(BadRequestException e) {
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}
