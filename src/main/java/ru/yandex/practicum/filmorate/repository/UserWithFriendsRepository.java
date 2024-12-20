package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserWithFriends;

import java.util.List;

@Repository
public class UserWithFriendsRepository extends BaseRepository<UserWithFriends> {
    private static final String FIND_ALL_QUERY = """
            SELECT
            	u.*,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            		WHERE f.FRIEND_ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            		WHERE f.FRIEND_ID IS NOT NULL)
            	END friends
            FROM
            	USERS u
            LEFT JOIN FRIENDS f ON
            	u.ID = f.USER_ID
            GROUP BY
            	u.ID
            """;
    private static final String FIND_ONE_BY_ID_QUERY = """
            SELECT
            	u.*,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            		WHERE f.FRIEND_ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            		WHERE f.FRIEND_ID IS NOT NULL)
            	END friends
            FROM
            	USERS u
            LEFT JOIN FRIENDS f ON
            	u.ID = f.USER_ID
            WHERE
            	u.ID = ?
            GROUP BY
            	u.ID
            """;
    private static final String FIND_ALL_FRIENDS_QUERY = """
            SELECT
            	u.*,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            	WHERE
            		f.FRIEND_ID IS NOT NULL) IS NULL
                            THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            	WHERE
            		f.FRIEND_ID IS NOT NULL)
            	END friends
            FROM
            	USERS u
            LEFT JOIN FRIENDS f ON
            	u.ID = f.USER_ID
            WHERE
            	u.ID IN (
            	SELECT
            		ID
            	FROM
            		USERS u
            	WHERE
            		u.ID IN (
            		SELECT
            			f.FRIEND_ID
            		FROM
            			USERS u
            		JOIN FRIENDS f ON
            			u.ID = f.USER_ID
            		WHERE
            			u.ID = ?))
            GROUP BY
            	u.ID
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT
            	u.*,
            	CASE
            		WHEN ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            	WHERE
            		f.FRIEND_ID IS NOT NULL) IS NULL
                                        THEN ARRAY[]
            		ELSE ARRAY_AGG(DISTINCT f.FRIEND_ID
            	ORDER BY
            		f.FRIEND_ID) FILTER (
            	WHERE
            		f.FRIEND_ID IS NOT NULL)
            	END friends
            FROM
            	USERS u
            LEFT JOIN FRIENDS f ON
            	u.ID = f.USER_ID
            WHERE
            	u.ID IN (
            	SELECT
            		f.FRIEND_ID
            	FROM
            		USERS u
            	LEFT JOIN FRIENDS f ON
            		u.ID = f.USER_ID
            	WHERE
            		u.ID = ?
            INTERSECT
            	SELECT
            		f.FRIEND_ID
            	FROM
            		USERS u
            	LEFT JOIN FRIENDS f ON
            		u.ID = f.USER_ID
            	WHERE
            		u.ID = ?)
            GROUP BY
            	u.ID
            """;

    public UserWithFriendsRepository(JdbcTemplate jdbc, RowMapper<UserWithFriends> mapper) {
        super(jdbc, mapper);
    }

    public List<UserWithFriends> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public UserWithFriends findOneById(Long userId) {
        return findOne(FIND_ONE_BY_ID_QUERY, userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь по данному id=%s не найден", userId)));
    }

    public List<UserWithFriends> getAllFriends(Long id) {
        return findAll(FIND_ALL_FRIENDS_QUERY, id);
    }

    public List<UserWithFriends> getCommonFriends(Long userId1, Long userId2) {
        return findAll(FIND_COMMON_FRIENDS_QUERY, userId1, userId2);
    }
}
