package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.UpdateUserDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
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
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
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
    private static final String FIND_ONE_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
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

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public User findOneById(Long userId) {
        return findOne(FIND_ONE_BY_ID_QUERY, userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь по данному id=%s не найден", userId)));
    }

    public Optional<User> findOneByEmail(String email) {
        return findOne(FIND_ONE_BY_EMAIL_QUERY, email);
    }

    public List<User> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public User createUser(CreateUserDTO createUserDTO) {
        Long id = insert(INSERT_QUERY,
                createUserDTO.getEmail(),
                createUserDTO.getLogin(),
                createUserDTO.getName(),
                createUserDTO.getBirthday());
        return findOneById(id);
    }

    public User updateUser(UpdateUserDTO updateUserDTO) {
        User user = findOneById(updateUserDTO.getId());
        Long id = updateUserDTO.getId();
        String email = updateUserDTO.getEmail();
        String login = updateUserDTO.getLogin();
        String name = updateUserDTO.getName();
        LocalDate birthday = updateUserDTO.getBirthday();
        update(UPDATE_QUERY,
                email == null ? user.getEmail() : email,
                login == null || login.isBlank() ? user.getLogin() : login,
                name == null || name.isBlank() ? user.getName() : name,
                birthday == null ? user.getBirthday() : birthday,
                id
        );
        return findOneById(id);
    }

    public List<User> getAllFriends(Long id) {
        return findAll(FIND_ALL_FRIENDS_QUERY, id);
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        return findAll(FIND_COMMON_FRIENDS_QUERY, userId1, userId2);
    }
}
