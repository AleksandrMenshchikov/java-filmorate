package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.user.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDTO;
import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static User mapToUser(CreateUserDTO createUserDTO) {
        return User.builder()
                .email(createUserDTO.getEmail())
                .birthday(createUserDTO.getBirthday())
                .name(createUserDTO.getName())
                .login(createUserDTO.getLogin())
                .build();
    }

    public static User mapToUser(UpdateUserDTO updateUserDTO) {
        return User.builder()
                .email(updateUserDTO.getEmail())
                .birthday(updateUserDTO.getBirthday())
                .name(updateUserDTO.getName())
                .login(updateUserDTO.getLogin())
                .id(updateUserDTO.getId())
                .build();
    }
}
