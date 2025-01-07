package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeFilmDbStorage implements LikeFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLikeFilmUser(Long idFilm, Long idUser) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilmId(idFilm));
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(idUser));
        if (user.isPresent() && film.isPresent()) {
                String filmLikeQuery = "INSERT INTO film_like(user_id, film_id) values (?, ?)";
                int rows = jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(filmLikeQuery);
                    stmt.setLong(1, idUser);
                    stmt.setLong(2, idFilm);
                    return stmt;
                });

                if (rows > 0) {
                    log.info("Пользователь с id = {} поставил лайк фильму с id = {}", idUser, idFilm);
                } else {
                    log.error("Ошибка при попытке поставить лайк фильму с id = {}", idFilm);
                    throw new ValidationException("Ошибка при попытке поставить лайк фильму с id = " + idFilm);
                }

        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idUser + " не найден");
        } else if (film.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idFilm + " не найден");
        }
    }

    @Override
    public void deleteLikeFilmUser(Long idFilm, Long idUser) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(idUser));
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilmId(idFilm));
        if (user.isPresent() && film.isPresent()) {
            String deleteFilmSql = "DELETE FROM film_like WHERE user_id = ? AND film_id = ?";
            jdbcTemplate.update(deleteFilmSql, idUser, idFilm);
            film.get().getLike().remove(idUser);
            log.info("Пользователь {} удалил лайк фильму {}", idUser, idFilm);

        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idUser + " не найден");
        } else if (film.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + idFilm + " не найден");
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String popularSql = "SELECT film_id FROM film_like";
        Comparator<Film> filmComparator = Comparator.comparingInt(film -> film.getLike().size());
        List<Film> popular = filmStorage.getFilms();
        return popular
                .stream()
                .filter(film -> !film.getLike().isEmpty())
                .sorted(filmComparator.reversed())
                .limit(count)
                .toList();
    }
}
