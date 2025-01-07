package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Override
    public void addFriends(User user, User friendUser) {
            String sqlQueryUser2 = "SELECT freind_id " +
                    "FROM friendship WHERE user_id = ?";

            String sqlQueryAddFriend = "INSERT INTO friendship(user_id, freind_id) values (?, ?)";

            List<Long> user2Ids = jdbcTemplate.queryForList(sqlQueryUser2, Long.class, user.getId());

            if (!user2Ids.contains(friendUser.getId())) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQueryAddFriend);
                    stmt.setLong(1, user.getId());
                    stmt.setLong(2, friendUser.getId());
                    return stmt;
                });
            }

            user.getFriends().add(friendUser.getId());

            log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", user.getId(), friendUser.getId());
    }

    @Override
    public void deleteFriend(User user, User friendUser) {
            String deleteFriendSql = "DELETE FROM friendship WHERE user_id = ? AND freind_id = ?";
            jdbcTemplate.update(deleteFriendSql, user.getId(), friendUser.getId());
            user.getFriends().remove(friendUser.getId());
            log.info("Пользователь {} удалил из друзей пользователя {}", user.getId(), friendUser.getId());
    }

    @Override
    public List<User> getListFriends(Long id) {
        String getListFriendsSql = "SELECT freind_id " +
                "FROM friendship WHERE user_id = ?";
        List<Long> idFriends = jdbcTemplate.queryForList(getListFriendsSql, Long.class, id);
        List<User> result = userStorage.getUsers().stream()
                .filter(user1 -> idFriends.contains(user1.getId()))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<User> getListCommonFriends(Long idUser, Long otherId) {
        List<User> listIdUser = getListFriends(idUser);
        List<User> listOtherId = getListFriends(otherId);
        listIdUser.retainAll(listOtherId);
        return listIdUser;
    }
}
