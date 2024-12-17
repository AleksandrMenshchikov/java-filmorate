package ru.yandex.practicum.filmorate.repository.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Genre> genres = Arrays.stream((Object[]) rs.getArray("genres").getArray()).map(o -> {
            try {
                return objectMapper.readValue((String) o, Genre.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        MPA mpa = null;
        try {
            mpa = objectMapper.readValue(rs.getString("mpa"), MPA.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Long> likes = Arrays.stream((Object[]) rs.getArray("likes")
                .getArray()).map((o -> (Long) o)).toList();

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .likes(likes)
                .genres(genres)
                .build();
    }
}

