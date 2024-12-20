package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.FilmWithLikesGenresMPA;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithLikesGenresMPARowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmsGenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MPARowMapper;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class,
        FilmRowMapper.class,
        MPARepository.class,
        MPARowMapper.class,
        GenreRepository.class,
        GenreRowMapper.class,
        FilmsGenreRepository.class,
        FilmsGenreRowMapper.class,
        LikeRepository.class,
        LikeRowMapper.class,
        FilmWithLikesGenresMPARepository.class,
        FilmWithLikesGenresMPARowMapper.class,
})
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final FilmWithLikesGenresMPARepository filmWithLikesGenresMPARepository;
    private final MPADTO mpadto = MPADTO.builder().id(1L).build();
    private final String name = "N";
    private final int duration = 100;
    private final LocalDate releaseDate = LocalDate.of(1900, 9, 10);
    private final String description = "D";
    private final List<GenreDTO> genres = List.of(GenreDTO.builder().id(1L).build());

    Long insertFilm() {
        CreateFilmDTO createFilmDTO = CreateFilmDTO.builder()
                .mpa(mpadto)
                .duration(duration)
                .releaseDate(releaseDate)
                .name(name)
                .description(description)
                .genres(genres)
                .build();
        return filmRepository.createFilm(FilmMapper.mapToFilm(createFilmDTO));
    }

    @Test
    void insertFilmTest() {
        Long id = insertFilm();
        Assertions.assertTrue(id > 0);
    }

    @Test
    void findOneById() {
        Long id = insertFilm();
        FilmWithLikesGenresMPA film = filmWithLikesGenresMPARepository.findOneById(id);
        Assertions.assertEquals(name, film.getName());
        Assertions.assertEquals(duration, film.getDuration());
        Assertions.assertEquals(releaseDate, film.getReleaseDate());
        Assertions.assertEquals(description, film.getDescription());
    }

    @Test
    void updateFilm() {
        Long id = insertFilm();
        FilmWithLikesGenresMPA f = filmWithLikesGenresMPARepository.findOneById(id);
        MPADTO mpadto = MPADTO.builder().id(2L).build();
        String name = f.getName() + "A";
        int duration = f.getDuration() + 1;
        LocalDate releaseDate = f.getReleaseDate().plusDays(1);
        String description = f.getDescription() + "A";
        List<GenreDTO> genres = List.of(GenreDTO.builder().id(2L).build());

        UpdateFilmDTO updateFilmDTO = UpdateFilmDTO.builder()
                .id(id)
                .name(name)
                .releaseDate(releaseDate)
                .mpa(mpadto)
                .description(description)
                .duration(duration)
                .genres(genres)
                .build();

        filmRepository.updateFilm(FilmMapper.mapToFilm(updateFilmDTO));
        FilmWithLikesGenresMPA film = filmWithLikesGenresMPARepository.findOneById(id);
        Assertions.assertEquals(name, film.getName());
        Assertions.assertEquals(duration, film.getDuration());
        Assertions.assertEquals(releaseDate, film.getReleaseDate());
        Assertions.assertEquals(description, film.getDescription());
    }

    @Test
    void findAll() {
        insertFilm();
        List<FilmWithLikesGenresMPA> films = filmWithLikesGenresMPARepository.findAll(Integer.MAX_VALUE, false);
        Assertions.assertEquals(1, films.size());
    }
}