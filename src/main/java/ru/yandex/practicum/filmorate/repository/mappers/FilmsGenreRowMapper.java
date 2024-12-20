package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmsGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmsGenreRowMapper implements RowMapper<FilmsGenre> {
    @Override
    public FilmsGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmsGenre.builder()
                .id(rs.getLong("id"))
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }
}
