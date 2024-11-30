package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserId(Integer id) {
        if (userStorage.findById(id).isEmpty()) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return userStorage.getUserId(id);
    }

    public Set<User> getListFriends(Integer id) {
        if (userStorage.findById(id).isEmpty()) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return userStorage.getListFriends(id);
    }

    public Set<User> getListCommonFriends(Integer idUser, Integer otherId) {
        if (userStorage.findById(idUser).isEmpty()) {
            log.error("Пользователь с id {} не найден", idUser);
            throw new NotFoundException("Пользователь с id " + idUser + " не найден");
        }
        if (userStorage.findById(otherId).isEmpty()) {
            log.error("Пользователь с id1 {} не найден", otherId);
            throw new NotFoundException("Пользователь с id " + otherId + " не найден");
        }
        if (idUser.equals(otherId)) {
            log.error("Это один и тот же пользователь с id {}", idUser);
            throw new ValidationException("Это один и тот же пользователь");
        }
        return userStorage.getListCommonFriends(idUser, otherId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updateUser) {
        if (userStorage.findById(updateUser.getId()).isEmpty()) {
            log.error("Пользователь с id {} не найден", updateUser.getId());
            throw new NotFoundException("Пользователь с id " + updateUser.getId() + " не найден");
        }
        return userStorage.updateUser(updateUser);
    }

    public User addFriends(Integer idUser, Integer idFriend) {
        if (userStorage.findById(idUser).isEmpty()) {
            log.error("Пользователь с id {} не найден", idUser);
            throw new NotFoundException("Пользователь с id " + idUser + " не найден");
        }
        if (userStorage.findById(idFriend).isEmpty()) {
            log.error("Пользователь с id {} не найден", idFriend);
            throw new NotFoundException("Пользователь с id " + idFriend + " не найден");
        }
        if (idUser.equals(idFriend)) {
            log.error("Это один и тот же пользователь с id {}", idUser);
            throw new ValidationException("Пользователь не может добавить в друзья самого себя");
        }
        log.info("Пользователь с id={} и пользователь с id={} стали друзьями", idUser, idFriend);
        return userStorage.addFriends(idUser, idFriend);
    }

    public User deleteFriend(Integer idUser, Integer idFriend) {
        if (userStorage.findById(idUser).isEmpty()) {
            log.error("Пользователь с id {} не найден", idUser);
            throw new NotFoundException("Пользователь с id " + idUser + " не найден");
        }
        if (userStorage.findById(idFriend).isEmpty()) {
            log.error("Пользователь с id {} не найден", idFriend);
            throw new NotFoundException("Пользователь с id " + idFriend + " не найден");
        }
        if (idUser.equals(idFriend)) {
            log.error("Это один и тот же пользователь с id {}", idUser);
            throw new ValidationException("Пользователь не может удалить из друзей самого себя");
        }
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}", idUser, idFriend);
        return userStorage.deleteFriend(idUser, idFriend);
    }
}
