package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbstorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTests {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    void removeUserTable() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    public void testGetUserById() {
        Optional<User> userOptional = Optional.of(userStorage.getUserId(1L));

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "gorych15200@yandex.ru")
                                .hasFieldOrPropertyWithValue("login", "gorych152")
                                .hasFieldOrPropertyWithValue("name", "Vladimir")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1998-07-25"))
                );
    }

    @Test
    public void testGetUsersAll() {
        Optional<List<User>> userListOptional = Optional.of(userStorage.getUsers());

        AssertionsForClassTypes.assertThat(userListOptional.get().size())
                .isEqualTo(3);
    }

    @Test
    public void testCreateUser() {
        User testUser = User.builder()
                .email("gorych300@yandex.ru")
                .login("gorych300")
                .name("Vladimir300")
                .birthday(LocalDate.of(1998, 07, 25))
                .build();
        userStorage.createUser(testUser);
        Optional<User> userOptional = Optional.of(userStorage.getUserId(4L));

        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("email", "gorych300@yandex.ru")
                                .hasFieldOrPropertyWithValue("login", "gorych300")
                                .hasFieldOrPropertyWithValue("name", "Vladimir300")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1998-07-25"))
                );
    }

    @Test
    public void testUpdateUser() {
        User testUser = User.builder()
                .id(1L)
                .email("gorych300@yandex.ru")
                .login("gorych300")
                .name("Vladimir300")
                .birthday(LocalDate.of(1998, 07, 25))
                .build();
        Optional<User> userOptional = Optional.of(userStorage.updateUser(testUser));
        AssertionsForClassTypes.assertThat(userOptional)
                .isPresent()
                .isEqualTo(Optional.of(testUser));
    }
}