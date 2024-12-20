package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDTO;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserWithFriendsMapper;
import ru.yandex.practicum.filmorate.model.UserWithFriends;
import ru.yandex.practicum.filmorate.repository.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserWithFriendsRowMapper;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class,
        UserRowMapper.class,
        FriendRepository.class,
        FriendRowMapper.class,
        UserWithFriendsRepository.class,
        UserWithFriendsRowMapper.class
})
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final UserWithFriendsRepository userDTORepository;
    private final String email = "email@email.ru";
    private final String login = "Login";
    private final String name = "N";
    private final LocalDate birthday = LocalDate.of(1990, 9, 10);

    ResponseUserDTO createUser() {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .birthday(birthday)
                .login(login)
                .email(email)
                .name(name)
                .build();
        Long id = userRepository.createUser(UserMapper.mapToUser(createUserDTO));
        return UserWithFriendsMapper.mapToUserDTO(userDTORepository.findOneById(id));
    }

    @Test
    void findOneById() {
        ResponseUserDTO u = createUser();
        UserWithFriends userWithFriends = userDTORepository.findOneById(u.getId());
        Assertions.assertEquals(name, userWithFriends.getName());
        Assertions.assertEquals(login, userWithFriends.getLogin());
        Assertions.assertEquals(email, userWithFriends.getEmail());
        Assertions.assertEquals(birthday, userWithFriends.getBirthday());
    }

    @Test
    void findAll() {
        createUser();
        List<UserWithFriends> users = userDTORepository.findAll();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void createUserTest() {
        ResponseUserDTO user = createUser();
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(login, user.getLogin());
        Assertions.assertEquals(email, user.getEmail());
        Assertions.assertEquals(birthday, user.getBirthday());
    }

    @Test
    void updateUser() {
        ResponseUserDTO u = createUser();
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
        userRepository.updateUser(UserMapper.mapToUser(updateUserDTO));
        UserWithFriends userWithFriends = userDTORepository.findOneById(id);
        Assertions.assertEquals(name, userWithFriends.getName());
        Assertions.assertEquals(email, userWithFriends.getEmail());
        Assertions.assertEquals(login, userWithFriends.getLogin());
        Assertions.assertEquals(birthday, userWithFriends.getBirthday());
    }
}