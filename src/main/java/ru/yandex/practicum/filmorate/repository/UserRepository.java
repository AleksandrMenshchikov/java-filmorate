package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.UpdateUserDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String FIND_ONE_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ONE_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

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
        User user = findOneById(id);
        user.setFriends(new HashSet<>());
        return user;
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
}
