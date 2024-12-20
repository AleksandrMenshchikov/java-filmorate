package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDTO;
import ru.yandex.practicum.filmorate.model.FilmWithLikesGenresMPA;

import java.util.List;

public class FilmWithLikesGenresMPAMapper {
    public static ResponseFilmDTO mapToResponseFilmDTO(FilmWithLikesGenresMPA filmWithLikesGenresMPA) {
        return ResponseFilmDTO.builder()
                .mpa(filmWithLikesGenresMPA.getMpa())
                .releaseDate(filmWithLikesGenresMPA.getReleaseDate())
                .likes(filmWithLikesGenresMPA.getLikes())
                .id(filmWithLikesGenresMPA.getId())
                .genres(filmWithLikesGenresMPA.getGenres())
                .description(filmWithLikesGenresMPA.getDescription())
                .duration(filmWithLikesGenresMPA.getDuration())
                .name(filmWithLikesGenresMPA.getName())
                .build();
    }

    public static List<ResponseFilmDTO> mapToResponseFilmDTOList(List<FilmWithLikesGenresMPA> filmWithLikesGenresMPAList) {
        return filmWithLikesGenresMPAList.stream().map(FilmWithLikesGenresMPAMapper::mapToResponseFilmDTO).toList();
    }
}
