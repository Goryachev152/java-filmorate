package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    void addFriends(Long idUser, Long idFriend);

    void deleteFriend(Long idUser, Long idFriend);

    List<User> getListFriends(Long id);

    List<User> getListCommonFriends(Long idUser, Long otherId);
}
