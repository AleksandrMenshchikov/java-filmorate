package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserWithFriendsMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.UserWithFriends;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.UserWithFriendsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserWithFriendsRepository userWithFriendsRepository;
    private final FriendRepository friendRepository;

    public ResponseUserDTO createUser(CreateUserDTO createUserDTO) {
        userRepository.findOneByEmail(createUserDTO.getEmail()).ifPresent((user) -> {
            throw new BadRequestException(String.format("Пользователь с email=%s уже существует.", user.getEmail()));
        });

        if (createUserDTO.getName() == null || createUserDTO.getName().isBlank()) {
            createUserDTO.setName(createUserDTO.getLogin());
        }

        Long id = userRepository.createUser(UserMapper.mapToUser(createUserDTO));
        return UserWithFriendsMapper.mapToUserDTO(userWithFriendsRepository.findOneById(id));
    }


    public ResponseUserDTO updateUser(UpdateUserDTO updateUserDTO) {
        UserWithFriends userWithFriends = userWithFriendsRepository.findOneById(updateUserDTO.getId());

        if (!userWithFriends.getEmail().equals(updateUserDTO.getEmail())) {
            userRepository.findOneByEmail(updateUserDTO.getEmail()).ifPresent((u) -> {
                throw new BadRequestException(String.format("Пользователь с email=%s уже существует.", u.getEmail()));
            });
        }

        String email = updateUserDTO.getEmail();
        if (email == null) {
            email = userWithFriends.getEmail();
        }

        String login = updateUserDTO.getLogin();
        if (login == null || login.isBlank()) {
            login = userWithFriends.getLogin();
        }

        String name = updateUserDTO.getName();
        if (name == null || name.isBlank()) {
            name = userWithFriends.getName();
        }

        LocalDate birthday = updateUserDTO.getBirthday();
        if (birthday == null) {
            birthday = userWithFriends.getBirthday();
        }

        userRepository.updateUser(UserMapper.mapToUser(updateUserDTO));
        return UserWithFriendsMapper.mapToUserDTO(userWithFriendsRepository.findOneById(userWithFriends.getId()));
    }

    public List<ResponseUserDTO> getAllUsers() {
        return UserWithFriendsMapper.mapToUserDTOList(userWithFriendsRepository.findAll());
    }

    public ResponseUserDTO getUserById(Long id) {
        return UserWithFriendsMapper.mapToUserDTO(userWithFriendsRepository.findOneById(id));
    }

    public List<ResponseUserDTO> getAllFriends(Long id) {
        userWithFriendsRepository.findOneById(id);
        return UserWithFriendsMapper.mapToUserDTOList(userWithFriendsRepository.getAllFriends(id));
    }

    public List<ResponseUserDTO> getCommonFriends(Long userId1, Long userId2) {
        return UserWithFriendsMapper.mapToUserDTOList(userWithFriendsRepository.getCommonFriends(userId1, userId2));
    }

    public ResponseUserDTO addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(String.format("Id=%s друга не должен быть равен id=%s пользователя.", friendId, userId));
        }

        UserWithFriends userWithFriends = userWithFriendsRepository.findOneById(userId);
        userWithFriendsRepository.findOneById(friendId);

        Optional<Friend> friend = friendRepository.findOneByUserIdAndFriendId(userId, friendId);

        if (friend.isEmpty()) {
            friendRepository.addFriend(userId, friendId);
            return UserWithFriendsMapper.mapToUserDTO(userWithFriendsRepository.findOneById(userId));
        } else {
            return UserWithFriendsMapper.mapToUserDTO(userWithFriends);
        }
    }

    public ResponseUserDTO deleteFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(String.format("Id=%s друга не должен быть равен id=%s пользователя.", friendId, userId));
        }

        UserWithFriends userWithFriends = userWithFriendsRepository.findOneById(userId);
        userWithFriendsRepository.findOneById(friendId);
        Optional<Friend> friend = friendRepository.findOneByUserIdAndFriendId(userId, friendId);
        friend.ifPresent(value -> friendRepository.delete(value.getId()));
        return UserWithFriendsMapper.mapToUserDTO(userWithFriends);
    }
}
