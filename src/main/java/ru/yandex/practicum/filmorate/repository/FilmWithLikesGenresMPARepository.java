package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmWithLikesGenresMPA;

import java.util.List;

@Repository
public class FilmWithLikesGenresMPARepository extends BaseRepository<FilmWithLikesGenresMPA> {
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

    public FilmWithLikesGenresMPARepository(JdbcTemplate jdbc, RowMapper<FilmWithLikesGenresMPA> mapper) {
        super(jdbc, mapper);
    }

    public FilmWithLikesGenresMPA findOneById(Long id) {
        return findOne(FIND_ONE_BY_ID_QUERY, id).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id=%s не найден.", id)));
    }

    public List<FilmWithLikesGenresMPA> findAll(Integer count, boolean sortByLikes) {
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
