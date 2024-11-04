package ru.yandex.practicum.filmorate.util;

import java.util.Map;

public class Id {
    public static Long generateId(Map<Long, ?> map) {
        long maxId = map
                .keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++maxId;
    }
}
