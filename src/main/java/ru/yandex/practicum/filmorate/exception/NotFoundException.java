package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        this(message, HttpStatus.NOT_FOUND);
    }

    private NotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
