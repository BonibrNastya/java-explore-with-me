package ru.practicum.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiError notFoundHandler(NotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalStateException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError illegalStateHandler(Throwable e) {
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.CONFLICT);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler({NumberFormatException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError numberFormatHandler(Throwable e) {
        ApiError apiError = new ApiError();
        apiError.getErrors().add(Arrays.toString(e.getStackTrace()));
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setReason(e.getMessage());
        apiError.setMessage(e.getLocalizedMessage());
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }
}
