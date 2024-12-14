package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.yandex.practicum.filmorate.exception.BaseException;

import java.time.Instant;

@RestControllerAdvice
public class CentralExceptionHandler {

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
    public ResponseEntity<ErrorResponse> handleInternalException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        HttpStatus httpStatus;

        if (e instanceof MethodArgumentNotValidException || e instanceof MethodArgumentTypeMismatchException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (e instanceof NoResourceFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(httpStatus.value()).body(ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build());
    }

    @Builder
    public record ErrorResponse(
            Instant timestamp,
            Integer status,
            String error,
            String message,
            String path) {
    }
}
