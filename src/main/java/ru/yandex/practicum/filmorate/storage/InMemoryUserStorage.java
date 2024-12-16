package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> userById = new HashMap<>();

    public List<User> getUsers() {
        return new ArrayList<>(userById.values());
    }

    public User getUserId(Integer id) {
        return userById.get(id);
    }

    public Set<User> getListFriends(Integer id) {
        User user = userById.get(id);
        return user.getFriends()
                .stream()
                .map(friendId -> userById.get(friendId))
                .collect(Collectors.toSet());
    }

    public Set<User> getListCommonFriends(Integer idUser, Integer otherId) {
        Set<Integer> userFriends = new HashSet<>(userById.get(idUser).getFriends());
        Set<Integer> otherFriends = new HashSet<>(userById.get(otherId).getFriends());
        userFriends.retainAll(otherFriends);
        return userFriends
                .stream()
                .map(friendId -> userById.get(friendId))
                .collect(Collectors.toSet());
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пользователь id {} использует логин вместо имени", user.getId());
            user = user.toBuilder()
                    .id(getNextId())
                    .name(user.getLogin())
                    .build();
        } else {
            user = user.toBuilder().id(getNextId()).build();
        }
        userById.put(user.getId(), user);
        log.info("Пользователь с id {} добавлен в сервис", user.getId());
        return user;
    }

    public User updateUser(User updateUser) {
        User oldUser;
        if (updateUser.getName().isBlank()) {
            oldUser = updateUser.toBuilder()
                    .id(updateUser.getId())
                    .email(updateUser.getEmail())
                    .login(updateUser.getLogin())
                    .name(updateUser.getLogin())
                    .birthday(updateUser.getBirthday())
                    .build();
        } else {
            oldUser = updateUser.toBuilder()
                    .id(updateUser.getId())
                    .email(updateUser.getEmail())
                    .login(updateUser.getLogin())
                    .name(updateUser.getName())
                    .birthday(updateUser.getBirthday())
                    .build();
        }
        userById.put(updateUser.getId(), oldUser);
        log.info("Пользователь с id {} добавлен в сервис", updateUser.getId());
        return oldUser;
    }

    public User addFriends(Integer idUser, Integer idFriend) {
        userById.get(idUser).getFriends().add(idFriend);
        userById.get(idFriend).getFriends().add(idUser);
        return getUserId(idFriend);
    }

    public User deleteFriend(Integer idUser, Integer idFriend) {
        userById.get(idUser).getFriends().remove(idFriend);
        userById.get(idFriend).getFriends().remove(idUser);
        return getUserId(idFriend);
    }

    public Optional<User> findById(Integer id) {
        if (userById.containsKey(id)) {
            return Optional.of(userById.get(id));
        } else {
            return Optional.empty();
        }
    }

    private int getNextId() {
        int currentMaxId = userById.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
