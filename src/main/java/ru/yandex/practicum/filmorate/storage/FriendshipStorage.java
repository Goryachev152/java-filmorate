package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    void addFriends(Integer idUser, Integer idFriend);

    void deleteFriend(Integer idUser, Integer idFriend);

    List<User> getListFriends(Integer id);

    List<User> getListCommonFriends(Integer idUser, Integer otherId);
}
