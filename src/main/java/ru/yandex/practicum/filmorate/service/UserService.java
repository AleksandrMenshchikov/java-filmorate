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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        User updatedUser = userRepository.updateUser(updateUserDTO);
        List<Friend> friends = friendRepository.findAllByUserId(updatedUser.getId());
        user.setFriends(new HashSet<>(friends.stream().map(Friend::getFriendId).toList()));
        return updatedUser;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Long> friends = friendRepository.findAllByUserId(user.getId()).stream().map(Friend::getFriendId).toList();
            user.setFriends(new HashSet<>(friends));
        }

        return users;
    }

    public User getUserById(Long id) {
        User user = userRepository.findOneById(id);
        List<Long> friends = friendRepository.findAllByUserId(user.getId()).stream().map(Friend::getFriendId).toList();
        user.setFriends(new HashSet<>(friends));
        return user;
    }

    public List<User> getAllFriends(Long id) {
        userRepository.findOneById(id);
        List<Long> friends = friendRepository.findAllByUserId(id).stream().map(Friend::getFriendId).toList();
        List<User> arrayList = new ArrayList<>();

        for (Long friend : friends) {
            arrayList.add(userRepository.findOneById(friend));
        }

        return arrayList;
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = getUserById(userId1);
        User user2 = getUserById(userId2);
        Set<Long> user1Friends = user1.getFriends();
        Set<Long> user2Friends = user2.getFriends();
        List<Long> list = user1Friends.stream().filter(user2Friends::contains).toList();
        return list.stream().map(this::getUserById).toList();
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
