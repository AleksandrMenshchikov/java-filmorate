package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.user.ResponseUserDTO;
import ru.yandex.practicum.filmorate.model.UserWithFriends;

import java.util.List;

public class UserWithFriendsMapper {
    public static ResponseUserDTO mapToUserDTO(UserWithFriends userWithFriends) {
        return ResponseUserDTO.builder()
                .email(userWithFriends.getEmail())
                .name(userWithFriends.getName())
                .login(userWithFriends.getLogin())
                .friends(userWithFriends.getFriends())
                .id(userWithFriends.getId())
                .birthday(userWithFriends.getBirthday())
                .build();
    }

    public static List<ResponseUserDTO> mapToUserDTOList(List<UserWithFriends> userWithFriendsList) {
        return userWithFriendsList.stream().map(UserWithFriendsMapper::mapToUserDTO).toList();
    }
}
