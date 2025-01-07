package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Override
    public void addFriends(Integer idUser, Integer idFriend) {
        Optional<User> mainUser = Optional.ofNullable(userStorage.getUserId(idUser));
        Optional<User> friendUser = Optional.ofNullable(userStorage.getUserId(idFriend));

        if (mainUser.isPresent() && friendUser.isPresent()) {

            String sqlQueryUser2 = "SELECT freind_id " +
                    "FROM friendship WHERE user_id = ?";

            String sqlQueryAddFriend = "INSERT INTO friendship(user_id, freind_id) values (?, ?)";

            List<Long> user2Ids = jdbcTemplate.queryForList(sqlQueryUser2, Long.class, idUser);

            if (!user2Ids.contains(idFriend)) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQueryAddFriend);
                    stmt.setLong(1, idUser);
                    stmt.setLong(2, idFriend);
                    return stmt;
                });
            }

            mainUser.get().getFriends().add(friendUser.get().getId());

            log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", idUser, idFriend);
        } else if (mainUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", idUser);
            throw new NotFoundException("Пользователь с id = " + idUser + " не найден");

        } else {
            log.error("Пользователь с id = {} не найден", idFriend);
            throw new NotFoundException("Пользователь с id = " + idFriend + " не найден");
        }
    }

    @Override
    public void deleteFriend(Integer idUser, Integer idFriend) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(idUser));
        Optional<User> friendUser = Optional.ofNullable(userStorage.getUserId(idFriend));
        if (user.isPresent() && friendUser.isPresent()) {
            String deleteFriendSql = "DELETE FROM friendship WHERE user_id = ? AND freind_id = ?";
            jdbcTemplate.update(deleteFriendSql, idUser, idFriend);
            user.get().getFriends().remove(idFriend);
            log.info("Пользователь {} удалил из друзей пользователя {}", idUser, idFriend);

        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idUser + " не найден");
        } else if (friendUser.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idFriend + " не найден");
        }
    }

    @Override
    public List<User> getListFriends(Integer id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(id));
        if (user.isPresent()) {
            String getListFriendsSql = "SELECT freind_id " +
                    "FROM friendship WHERE user_id = ?";
            List<Integer> idFriends = jdbcTemplate.queryForList(getListFriendsSql, Integer.class, id);
            List<User> result = userStorage.getUsers().stream()
                    .filter(user1 -> idFriends.contains(user1.getId()))
                    .collect(Collectors.toList());
            return result;
        } else {
            throw new NotFoundException("Пользоваетль " + id + " не найден");
        }
    }

    @Override
    public List<User> getListCommonFriends(Integer idUser, Integer otherId) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(idUser));
        Optional<User> friendUser = Optional.ofNullable(userStorage.getUserId(otherId));
        if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idUser + " не найден");
        } else if (friendUser.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + otherId + " не найден");
        }
        List<User> listIdUser = getListFriends(idUser);
        List<User> listOtherId = getListFriends(otherId);
        listIdUser.retainAll(listOtherId);
        return listIdUser;
    }
}
