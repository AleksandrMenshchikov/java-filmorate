package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String FIND_ONE_BY_FILM_ID_AND_USER_ID_QUERY = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Like> findOneByFilmIdAndUserId(Long filmId, Long userId) {
        try {
            return findOne(FIND_ONE_BY_FILM_ID_AND_USER_ID_QUERY, filmId, userId);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Like> findAllByFilmId(Long filmId) {
        return findAll(FIND_ALL_BY_FILM_ID_QUERY, filmId);
    }

    public void addLike(Long filmId, Long genreId) {
        insert(INSERT_QUERY, filmId, genreId);
    }

    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }
}
