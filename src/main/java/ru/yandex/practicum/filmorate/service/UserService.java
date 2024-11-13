package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        Optional<User> optionalUser = inMemoryUserStorage.save(user);

        if (optionalUser.isEmpty()) {
            throw new InternalServerErrorException("Произошла ошибка при создании пользователя.");
        }

        return optionalUser.get();
    }


    public User updateUser(User user) {
        Long id = user.getId();

        if (id == null) {
            throw new BadRequestException("Поле id должно быть в теле запроса.");
        }

        User userStorage = getUserById(id);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setFriends(userStorage.getFriends());
        Optional<User> optionalUser = inMemoryUserStorage.save(user);

        if (optionalUser.isEmpty()) {
            throw new InternalServerErrorException("Произошла ошибка при обновлении пользователя.");
        }

        return optionalUser.get();
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser = inMemoryUserStorage.findOneById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь по данному id=%s не найден", id));
        }

        return optionalUser.get();
    }

    public List<User> getAllFriends(Long id) {
        User user = getUserById(id);
        return user.getFriends().stream().map(this::getUserById).toList();
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        List<Long> list = userFriends.stream().filter(otherUserFriends::contains).toList();
        return list.stream().map(this::getUserById).toList();
    }

    public User updateUserFriends(Action action, Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new BadRequestException(String.format("Id=%s друга не должен быть равен id=%s пользователя.", friendId, id));
        }

        User user = getUserById(id);
        User friend = getUserById(friendId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();

        if (action.equals(Action.ADD)) {
            userFriends.add(friend.getId());
            friendFriends.add(user.getId());
        } else if (action.equals(Action.REMOVE)) {
            userFriends.remove(friend.getId());
            friendFriends.remove(user.getId());
        }

        Optional<User> optionalUser = inMemoryUserStorage.save(user);
        Optional<User> optionalFriend = inMemoryUserStorage.save(friend);

        if (optionalUser.isEmpty() || optionalFriend.isEmpty()) {
            throw new InternalServerErrorException("Произошла ошибка при обновлении пользователя.");
        }

        return user;
    }
}
