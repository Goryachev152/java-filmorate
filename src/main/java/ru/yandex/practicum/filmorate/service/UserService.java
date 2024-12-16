package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserId(Integer id) {
        validNotFoundUser(id);
        return userStorage.getUserId(id);
    }

    public Set<User> getListFriends(Integer id) {
        validNotFoundUser(id);
        return userStorage.getListFriends(id);
    }

    public Set<User> getListCommonFriends(Integer idUser, Integer otherId) {
        validNotFoundUser(idUser);
        validNotFoundUser(otherId);
        validEqualsUser(idUser, otherId);
        return userStorage.getListCommonFriends(idUser, otherId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updateUser) {
        validNotFoundUser(updateUser.getId());
        return userStorage.updateUser(updateUser);
    }

    public User addFriends(Integer idUser, Integer idFriend) {
        validNotFoundUser(idUser);
        validNotFoundUser(idFriend);
        validEqualsUser(idUser, idFriend);
        log.info("Пользователь с id={} и пользователь с id={} стали друзьями", idUser, idFriend);
        return userStorage.addFriends(idUser, idFriend);
    }

    public User deleteFriend(Integer idUser, Integer idFriend) {
        validNotFoundUser(idUser);
        validNotFoundUser(idFriend);
        validEqualsUser(idUser, idFriend);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}", idUser, idFriend);
        return userStorage.deleteFriend(idUser, idFriend);
    }

    private void validNotFoundUser(Integer id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private void validEqualsUser(Integer idUser, Integer idFriend) {
        if (idUser.equals(idFriend)) {
            throw new ValidationException("Это один и тот же пользователь");
        }
    }
}
