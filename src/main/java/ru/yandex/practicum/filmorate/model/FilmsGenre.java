package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmsGenre {
    private Long id;
    private Long filmId;
    private Long genreId;
}
