package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
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

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(badRequest.value()).body(ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(badRequest.value())
                .error(badRequest.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(notFound.value()).body(ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(notFound.value())
                .error(notFound.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInternalException(Exception e, HttpServletRequest request) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(internalServerError.value()).body(ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(internalServerError.value())
                .error(internalServerError.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build());
    }
}
