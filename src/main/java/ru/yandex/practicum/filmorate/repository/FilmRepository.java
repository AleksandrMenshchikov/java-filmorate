package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ONE_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Long insertFilm(CreateFilmDTO createFilmDTO) {
        return insert(INSERT_QUERY,
                createFilmDTO.getName(),
                createFilmDTO.getDescription(),
                createFilmDTO.getReleaseDate(),
                createFilmDTO.getDuration(),
                createFilmDTO.getMpa().getId());
    }

    public Film findOneById(Long id) {
        return findOne(FIND_ONE_BY_ID_QUERY, id).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id=%s не найден.", id)));
    }

    public Film updateFilm(UpdateFilmDTO updateFilmDTO) {
        Long id = updateFilmDTO.getId();
        String name = updateFilmDTO.getName();
        String description = updateFilmDTO.getDescription();
        LocalDate releaseDate = updateFilmDTO.getReleaseDate();
        Integer duration = updateFilmDTO.getDuration();
        Long mpaId = updateFilmDTO.getMpa().getId();
        Film film = findOneById(id);

        update(UPDATE_QUERY,
                name == null || name.isBlank() ? film.getName() : name,
                description == null || description.isBlank() ? film.getDescription() : description,
                releaseDate == null ? film.getReleaseDate() : releaseDate,
                duration == null ? film.getDuration() : duration,
                mpaId,
                id
        );

        return findOneById(id);
    }

    public List<Film> findAll() {
        return findAll(FIND_ALL_QUERY);
    }
}
