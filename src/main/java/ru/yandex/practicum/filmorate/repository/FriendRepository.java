package ru.yandex.practicum.filmorate.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendRepository extends BaseRepository<Friend> {
    private static final String FIND_ALL_BY_USER_ID_QUERY = "SELECT * FROM friends WHERE user_id = ?";
    private static final String FIND_ONE_BY_USER_ID_AND_FRIEND_ID_QUERY = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM friends WHERE id = ?";

    public FriendRepository(JdbcTemplate jdbc, RowMapper<Friend> mapper) {
        super(jdbc, mapper);
    }

    public List<Friend> findAllByUserId(Long userId) {
        return findAll(FIND_ALL_BY_USER_ID_QUERY, userId);
    }

    public Optional<Friend> findOneByUserIdAndFriendId(Long userId, Long friendId) {
        try {
            return findOne(FIND_ONE_BY_USER_ID_AND_FRIEND_ID_QUERY, userId, friendId);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public void addFriend(Long userId, Long friendId) {
        insert(INSERT_QUERY, userId, friendId);
    }

    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }
}
