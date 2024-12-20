package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmMapper {
    public static Film mapToFilm(CreateFilmDTO createFilmDTO) {
        return Film.builder()
                .releaseDate(createFilmDTO.getReleaseDate())
                .description(createFilmDTO.getDescription())
                .mpaId(createFilmDTO.getMpa().getId())
                .duration(createFilmDTO.getDuration())
                .name(createFilmDTO.getName())
                .build();
    }

    public static Film mapToFilm(UpdateFilmDTO updateFilmDTO) {
        return Film.builder()
                .releaseDate(updateFilmDTO.getReleaseDate())
                .description(updateFilmDTO.getDescription())
                .mpaId(updateFilmDTO.getMpa().getId())
                .duration(updateFilmDTO.getDuration())
                .name(updateFilmDTO.getName())
                .id(updateFilmDTO.getId())
                .build();
    }
}
