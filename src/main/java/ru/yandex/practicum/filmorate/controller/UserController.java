package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Id;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    private boolean isValidEmail(String email) {
        return email != null && !email.isBlank() && email.contains("@");
    }

    private boolean isValidLogin(String login) {
        return login != null && !login.isBlank();
    }

    private boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    private boolean isValidBirthday(LocalDate birthday) {
        return LocalDate.now().isAfter(birthday);
    }

    private void throwException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();

        if (!isValidEmail(email)) {
            throwException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (!isValidLogin(login)) {
            throwException("Логин не может быть пустым и содержать пробелы");
        }

        if (name == null || name.isBlank()) {
            user.setName(login);
        }

        if (!isValidBirthday(birthday)) {
            throwException("Дата рождения не может быть в будущем");
        }

        user.setId(Id.generateId(users));
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        Long id = user.getId();
        String email = user.getEmail();
        String login = user.getLogin();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();
        User currentUser = users.get(id);

        if (currentUser != null) {
            if (isValidEmail(email)) {
                currentUser.setEmail(email);
            }

            if (isValidLogin(login)) {
                currentUser.setLogin(login);
            }

            if (isValidName(name)) {
                currentUser.setName(name);
            }

            if (isValidBirthday(birthday)) {
                currentUser.setBirthday(birthday);
            }
        } else {
            throwException("Пользователь по данному id не найден");
        }

        return currentUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }
}