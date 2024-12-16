package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    List<User> getUsers();

    User createUser(User user);

    User updateUser(User updateUser);

    Optional<User> findById(Integer id);

    User getUserId(Integer id);

    User addFriends(Integer idUser, Integer idFriend);

    User deleteFriend(Integer idUser, Integer idFriend);

    Set<User> getListFriends(Integer id);

    Set<User> getListCommonFriends(Integer idUser, Integer otherId);
}
