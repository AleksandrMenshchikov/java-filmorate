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
    private static final String FIND_ONE_BY_ID_QUERY = """
            SELECT
            	f.ID ,
            	f.NAME ,
            	f.DESCRIPTION ,
            	f.RELEASE_DATE,
            	f.DURATION,
            	JSON_OBJECT('id': m.ID,
            	'name': m.NAME)::varchar mpa,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT g.ID) FILTER (
            		WHERE g.ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT JSON_OBJECT('id': g.ID,
            		'name': g.NAME)::varchar)
            	END genres,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT l.USER_ID
            	ORDER BY
            		l.USER_ID) FILTER (
            		WHERE l.USER_ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT l.USER_ID
            	ORDER BY
            		l.USER_ID) FILTER (
            		WHERE l.USER_ID IS NOT NULL)
            	END likes
            FROM
            	FILMS f
            JOIN MPA m ON
            	f.MPA_ID = m.ID
            LEFT JOIN LIKES l ON
            	f.ID = l.FILM_ID
            LEFT JOIN FILMS_GENRES fg ON
            	f.ID = fg.FILM_ID
            LEFT JOIN GENRES g ON
            	g.ID = fg.GENRE_ID
            WHERE
            	f.ID = ?
            GROUP BY
            	f.ID
            """;
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = """
            SELECT
            	f.ID ,
            	f.NAME ,
            	f.DESCRIPTION ,
            	f.RELEASE_DATE,
            	f.DURATION,
            	JSON_OBJECT('id': m.ID,
            	'name': m.NAME)::varchar mpa,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT g.ID) FILTER (
            		WHERE g.ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT JSON_OBJECT('id': g.ID,
            		'name': g.NAME)::varchar)
            	END genres,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT l.USER_ID
            	ORDER BY
            		l.USER_ID) FILTER (
            		WHERE l.USER_ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT l.USER_ID
            	ORDER BY
            		l.USER_ID) FILTER (
            		WHERE l.USER_ID IS NOT NULL)
            	END likes
            FROM
            	FILMS f
            JOIN MPA m ON
            	f.MPA_ID = m.ID
            LEFT JOIN LIKES l ON
            	f.ID = l.FILM_ID
            LEFT JOIN FILMS_GENRES fg ON
            	f.ID = fg.FILM_ID
            LEFT JOIN GENRES g ON
            	g.ID = fg.GENRE_ID
            GROUP BY
            	f.ID
            """;

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

    public List<Film> findAll(Integer count, boolean sortByLikes) {
        String query;
        String str1 = """
                ORDER BY ARRAY_LENGTH(ARRAY_AGG(l.USER_ID) FILTER (
                WHERE l.USER_ID IS NOT NULL)) DESC
                LIMIT ?
                """;
        String str2 = """
                ORDER BY f.id
                LIMIT ?
                """;

        if (sortByLikes) {
            query = FIND_ALL_QUERY + str1;
        } else {
            query = FIND_ALL_QUERY + str2;
        }

        return findAll(query, count < 0 ? 0 : count);
    }
}
