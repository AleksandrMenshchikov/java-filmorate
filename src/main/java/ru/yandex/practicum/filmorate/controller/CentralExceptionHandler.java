package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BaseException;

import java.time.Instant;

@RestControllerAdvice
public class CentralExceptionHandler {

    @Builder
    public record ErrorResponse(
            Instant timestamp,
            Integer status,
            String error,
            String message,
            String path) {
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomException(BaseException e, HttpServletRequest request) {
        return ResponseEntity.status(e.getStatus().value()).body(ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(e.getStatus().value())
                .error(e.getStatus().getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build());
    }
}
