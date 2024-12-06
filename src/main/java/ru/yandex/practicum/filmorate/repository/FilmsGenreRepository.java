package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmsGenre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmsGenreRepository extends BaseRepository<FilmsGenre> {
    private static final String FIND_ONE_BY_FILM_ID_AND_GENRE_ID_QUERY = "SELECT * FROM films_genres WHERE film_id = ? AND genre_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = "SELECT * FROM films_genres WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM films_genres WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE films_genres SET film_id = ?, genre_id = ?";

    public FilmsGenreRepository(JdbcTemplate jdbc, RowMapper<FilmsGenre> mapper) {
        super(jdbc, mapper);
    }

    public Optional<FilmsGenre> findOneByFilmIdAndGenreId(Long filmId, Long genreId) {
        try {
            return findOne(FIND_ONE_BY_FILM_ID_AND_GENRE_ID_QUERY, filmId, genreId);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<FilmsGenre> findAllByFilmId(Long filmId) {
        return findAll(FIND_ALL_BY_FILM_ID_QUERY, filmId);
    }

    public void addGenre(Long filmId, Long genreId) {
        insert(INSERT_QUERY, filmId, genreId);
    }

    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }

    public void update(Long filmId, Long genreId) {
        update(UPDATE_QUERY, filmId, genreId);
    }
}
