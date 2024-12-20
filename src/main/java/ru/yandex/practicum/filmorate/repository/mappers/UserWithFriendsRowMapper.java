package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserWithFriends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class UserWithFriendsRowMapper implements RowMapper<UserWithFriends> {
    @Override
    public UserWithFriends mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Long> friends = Arrays.stream((Object[]) rs.getArray("friends")
                .getArray()).map((o -> (Long) o)).toList();

        return UserWithFriends.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(friends)
                .build();
    }
}
