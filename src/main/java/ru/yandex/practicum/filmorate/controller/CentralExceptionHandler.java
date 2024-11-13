package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BaseException;

import java.time.Instant;

@RestControllerAdvice
public class CentralExceptionHandler {

    public record ErrorResponse(
            Instant timestamp,
            Integer status,
            String error,
            String message,
            String path) {
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(BaseException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                e.getStatus().value(),
                e.getStatus().getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(e.getStatus().value()).body(errorResponse);
    }
}
