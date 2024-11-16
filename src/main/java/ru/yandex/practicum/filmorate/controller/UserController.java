package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> userMap = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return userMap.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пользователь id {} использует логин вместо имени", user.getId());
            user = user.toBuilder()
                    .id(getNextId())
                    .name(user.getLogin())
                    .build();
            userMap.put(user.getId(), user);
            log.info("Пользователь с id {} добавлен в сервис", user.getId());
            return user;
        } else {
            user = user.toBuilder().id(getNextId()).build();
            userMap.put(user.getId(), user);
            log.info("Пользователь с id {} добавлен в сервис", user.getId());
            return user;
        }

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) {
        if (!userMap.containsKey(updateUser.getId())) {
            log.error("Пользователь с id {} не найден", updateUser.getId());
            throw new ValidationException("Пользователь с id " + updateUser.getId() + " не найден");
        }
        if (updateUser.getName().isBlank()) {
            User oldUser = updateUser.toBuilder()
                    .id(updateUser.getId())
                    .email(updateUser.getEmail())
                    .login(updateUser.getLogin())
                    .name(updateUser.getLogin())
                    .birthday(updateUser.getBirthday())
                    .build();
            userMap.put(updateUser.getId(), oldUser);
            log.info("Пользователь с id {} добавлен в сервис", updateUser.getId());
            return oldUser;
        } else {
            User oldUser = updateUser.toBuilder()
                    .id(updateUser.getId())
                    .email(updateUser.getEmail())
                    .login(updateUser.getLogin())
                    .name(updateUser.getName())
                    .birthday(updateUser.getBirthday())
                    .build();
            userMap.put(updateUser.getId(), oldUser);
            log.info("Пользователь с id {} добавлен в сервис", updateUser.getId());
            return oldUser;
        }
    }

    private int getNextId() {
        int currentMaxId = userMap.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
