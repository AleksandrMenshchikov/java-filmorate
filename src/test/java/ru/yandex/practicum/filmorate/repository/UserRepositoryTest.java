package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.UpdateUserDTO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class,
        UserRowMapper.class,
        FriendRepository.class,
        FriendRowMapper.class
})
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final String email = "email@email.ru";
    private final String login = "Login";
    private final String name = "N";
    private final LocalDate birthday = LocalDate.of(1990, 9, 10);

    User createUser() {
        return userRepository.createUser(CreateUserDTO.builder()
                .birthday(birthday)
                .login(login)
                .email(email)
                .name(name)
                .build());
    }

    @Test
    void findOneById() {
        User u = createUser();
        User user = userRepository.findOneById(u.getId());
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(login, user.getLogin());
        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(birthday, user.getBirthday());
    }

    @Test
    void findAll() {
        createUser();
        List<User> users = userRepository.findAll();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void createUserTest() {
        User user = createUser();
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(login, user.getLogin());
        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(birthday, user.getBirthday());
    }

    @Test
    void updateUser() {
        User u = createUser();
        Long id = u.getId();
        String name = u.getName() + "A";
        String email = u.getEmail() + "a";
        String login = u.getLogin() + "a";
        LocalDate birthday = u.getBirthday().plusDays(1);

        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .birthday(birthday)
                .id(id)
                .name(name)
                .email(email)
                .login(login)
                .build();

        User user = userRepository.updateUser(updateUserDTO);
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(login, user.getLogin());
        Assertions.assertEquals(birthday, user.getBirthday());
    }
}