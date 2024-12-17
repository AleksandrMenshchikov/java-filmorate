package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateUserDTO;
import ru.yandex.practicum.filmorate.dto.UpdateUserDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public User createUser(CreateUserDTO createUserDTO) {
        userRepository.findOneByEmail(createUserDTO.getEmail()).ifPresent((user) -> {
            throw new BadRequestException(String.format("Пользователь с email=%s уже существует.", user.getEmail()));
        });

        if (createUserDTO.getName() == null || createUserDTO.getName().isBlank()) {
            createUserDTO.setName(createUserDTO.getLogin());
        }

        return userRepository.createUser(createUserDTO);
    }


    public User updateUser(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findOneById(updateUserDTO.getId());

        if (!user.getEmail().equals(updateUserDTO.getEmail())) {
            userRepository.findOneByEmail(updateUserDTO.getEmail()).ifPresent((u) -> {
                throw new BadRequestException(String.format("Пользователь с email=%s уже существует.", u.getEmail()));
            });
        }

        return userRepository.updateUser(updateUserDTO);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findOneById(id);
    }

    public List<User> getAllFriends(Long id) {
        userRepository.findOneById(id);
        return userRepository.getAllFriends(id);
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        return userRepository.getCommonFriends(userId1, userId2);
    }

    public User addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(String.format("Id=%s друга не должен быть равен id=%s пользователя.", friendId, userId));
        }

        User user = userRepository.findOneById(userId);
        userRepository.findOneById(friendId);

        Optional<Friend> friend = friendRepository.findOneByUserIdAndFriendId(userId, friendId);

        if (friend.isEmpty()) {
            friendRepository.addFriend(userId, friendId);
            return userRepository.findOneById(userId);
        } else {
            return user;
        }
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(String.format("Id=%s друга не должен быть равен id=%s пользователя.", friendId, userId));
        }

        User user = userRepository.findOneById(userId);
        userRepository.findOneById(friendId);
        Optional<Friend> friend = friendRepository.findOneByUserIdAndFriendId(userId, friendId);
        friend.ifPresent(value -> friendRepository.delete(value.getId()));
        return user;
    }
}
