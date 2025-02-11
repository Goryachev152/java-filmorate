package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserId(Long id) {
        return userStorage.getUserId(id);
    }

    public List<User> getListFriends(Long id) {
        validNotFoundUser(id);
        return friendshipStorage.getListFriends(id);
    }

    public List<User> getListCommonFriends(Long idUser, Long otherId) {
        validEqualsUser(idUser, otherId);
        validNotFoundUser(idUser);
        validNotFoundUser(otherId);
        return friendshipStorage.getListCommonFriends(idUser, otherId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updateUser) {
        return userStorage.updateUser(updateUser);
    }

    public void addFriends(Long idUser, Long idFriend) {
        validEqualsUser(idUser, idFriend);
        User user = validNotFoundUser(idUser);
        User friendUser = validNotFoundUser(idFriend);
        friendshipStorage.addFriends(user, friendUser);
    }

    public void deleteFriend(Long idUser, Long idFriend) {
        validEqualsUser(idUser, idFriend);
        User user = validNotFoundUser(idUser);
        User friendUser = validNotFoundUser(idFriend);
        friendshipStorage.deleteFriend(user, friendUser);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}", idUser, idFriend);

    }

    private void validEqualsUser(Long idUser, Long idFriend) {
        if (idUser.equals(idFriend)) {
            throw new ValidationException("Это один и тот же пользователь");
        }
    }

    private User validNotFoundUser(Long id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(id));
        if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + id + " не найден");
        } else {
            return user.get();
        }
    }
}
