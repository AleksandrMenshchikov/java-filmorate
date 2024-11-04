package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(Id.generateId(users));
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User currentUser = users.get(user.getId());

        if (currentUser != null) {
            currentUser.setEmail(user.getEmail());
            currentUser.setLogin(user.getLogin());

            if (user.getName() != null && !user.getName().isBlank()
                    && !currentUser.getName().equals(user.getName())) {
                currentUser.setName(user.getName());
            }

            currentUser.setBirthday(user.getBirthday());
        } else {
            throw new ValidationException("Пользователь по данному id не найден");
        }

        return currentUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }
}