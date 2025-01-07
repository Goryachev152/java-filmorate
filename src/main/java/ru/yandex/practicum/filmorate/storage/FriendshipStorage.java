package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    void addFriends(User user, User friendUser);

    void deleteFriend(User user, User friendUser);

    List<User> getListFriends(Long id);

    List<User> getListCommonFriends(Long idUser, Long otherId);
}
