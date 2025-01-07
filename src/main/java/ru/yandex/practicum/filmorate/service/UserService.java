package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

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
        //validNotFoundUser(id);
        return friendshipStorage.getListFriends(id);
    }

    public List<User> getListCommonFriends(Long idUser, Long otherId) {
        validEqualsUser(idUser, otherId);
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
        friendshipStorage.addFriends(idUser, idFriend);
    }

    public void deleteFriend(Long idUser, Long idFriend) {
        validEqualsUser(idUser, idFriend);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}", idUser, idFriend);
        friendshipStorage.deleteFriend(idUser, idFriend);
    }

    private void validEqualsUser(Long idUser, Long idFriend) {
        if (idUser.equals(idFriend)) {
            throw new ValidationException("Это один и тот же пользователь");
        }
    }
}
