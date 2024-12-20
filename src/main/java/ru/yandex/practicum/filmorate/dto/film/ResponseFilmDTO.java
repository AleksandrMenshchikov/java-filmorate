package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ResponseFilmDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<Long> likes; // usersIds
    private List<Genre> genres;
    private MPA mpa;
}
