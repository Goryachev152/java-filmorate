package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, userRowMapper::mapRow);
    }

    @Override
    public User getUserId(Long id) {
        String getUserSql = "SELECT id, email, login, name, birthday " +
                "FROM users WHERE id = ?";
        Optional<User> resultUser;
        try {
            resultUser = Optional.ofNullable(jdbcTemplate.queryForObject(getUserSql,
                    userRowMapper::mapRow, id));
        } catch (EmptyResultDataAccessException e) {
            resultUser = Optional.empty();
        }
        if (resultUser.isPresent()) {
            return resultUser.get();
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String createSql = "INSERT INTO users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пользователь  {} использует логин вместо имени", user.getEmail());
            user = user.toBuilder()
                    .name(user.getLogin())
                    .build();
        }
        User finalUser = user;
        Long userId;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createSql, new String[]{"id"});
            stmt.setString(1, finalUser.getEmail());
            stmt.setString(2, finalUser.getLogin());
            stmt.setString(3, finalUser.getName());
            stmt.setString(4, finalUser.getBirthday().toString());
            return stmt;
        }, keyHolder);
        if (!Objects.nonNull(keyHolder.getKey())) {
            throw new NotFoundException("Ошибка добавления пользователя в таблицу");
        } else {
            userId = keyHolder.getKey().longValue();
        }
        log.info("Пользователь с id {} добавлен в сервис", userId);
        return User.builder()
                .id(userId)
                .email(finalUser.getEmail())
                .login(finalUser.getLogin())
                .name(finalUser.getName())
                .birthday(finalUser.getBirthday())
                .build();
    }

    @Override
    public User updateUser(User updateUser) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Long userId;
        String updateSql = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(updateSql, new String[]{"id"});
            stmt.setString(1, updateUser.getEmail());
            stmt.setString(2, updateUser.getLogin());
            stmt.setString(3, updateUser.getName());
            stmt.setString(4, updateUser.getBirthday().toString());
            stmt.setLong(5, updateUser.getId());
            return stmt;
        }, keyHolder);
        if (Objects.nonNull(keyHolder.getKey())) {
            userId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка обновления пользователя");
        }
        User resultUser = User.builder()
                .id(userId)
                .email(updateUser.getEmail())
                .login(updateUser.getLogin())
                .name(updateUser.getName())
                .birthday(updateUser.getBirthday())
                .build();
        if (rows > 0) {
            log.info("Пользователь с id = {} успешно обновлён", userId);
            return resultUser;

        } else {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}
