package ru.yandex.practicum.filmorate.model.enums;

import lombok.Getter;

@Getter
public enum MPAType {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String value;

    MPAType(String value) {
        this.value = value;
    }
}
