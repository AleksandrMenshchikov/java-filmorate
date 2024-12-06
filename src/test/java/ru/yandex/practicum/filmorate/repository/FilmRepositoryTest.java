package ru.yandex.practicum.filmorate.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
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
        LikeRowMapper.class
})
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final MPADTO mpadto = MPADTO.builder().id(1L).build();
    private final String name = "N";
    private final int duration = 100;
    private final LocalDate releaseDate = LocalDate.of(1900, 9, 10);
    private final String description = "D";
    @Getter
    private final List<GenreDTO> genres = List.of(GenreDTO.builder().id(1L).build());

    Film createFilm() {
        return filmRepository.createFilm(CreateFilmDTO.builder()
                .mpa(mpadto)
                .duration(duration)
                .releaseDate(releaseDate)
                .name(name)
                .description(description)
                .genres(genres)
                .build());
    }

    @Test
    void createFilmTest() {
        Film film = createFilm();
        Assertions.assertEquals(name, film.getName());
        Assertions.assertEquals(duration, film.getDuration());
        Assertions.assertEquals(releaseDate, film.getReleaseDate());
        Assertions.assertEquals(description, film.getDescription());
        Assertions.assertEquals(genres.get(0).getId(), film.getGenres().get(0).getId());
    }

    @Test
    void findOneById() {
        Film f = createFilm();
        Film film = filmRepository.findOneById(f.getId());
        Assertions.assertEquals(name, film.getName());
        Assertions.assertEquals(duration, film.getDuration());
        Assertions.assertEquals(releaseDate, film.getReleaseDate());
        Assertions.assertEquals(description, film.getDescription());
        Assertions.assertEquals(genres.get(0).getId(), film.getGenres().get(0).getId());
    }

    @Test
    void updateFilm() {
        Film f = createFilm();
        Long id = f.getId();
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

        Film film = filmRepository.updateFilm(updateFilmDTO);
        Assertions.assertEquals(name, film.getName());
        Assertions.assertEquals(duration, film.getDuration());
        Assertions.assertEquals(releaseDate, film.getReleaseDate());
        Assertions.assertEquals(description, film.getDescription());
        Assertions.assertEquals(genres.get(0).getId(), film.getGenres().get(0).getId());
    }

    @Test
    void findAll() {
        createFilm();
        List<Film> films = filmRepository.findAll();
        Assertions.assertEquals(1, films.size());
    }
}