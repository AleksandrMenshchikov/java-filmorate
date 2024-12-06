package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;

@Getter
public enum GenreType {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String value;

    GenreType(String value) {
        this.value = value;
    }
}
