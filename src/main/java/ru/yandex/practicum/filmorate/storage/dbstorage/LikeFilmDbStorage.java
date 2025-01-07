package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeFilmStorage;

import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class LikeFilmDbStorage implements LikeFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public LikeFilmDbStorage(JdbcTemplate jdbcTemplate, @Lazy FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public void addLikeFilmUser(Long idFilm, Long idUser) {
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
            throw new ValidationException("Ошибка при попытке поставить лайк фильму с id = " + idFilm);
        }
    }

    @Override
    public void deleteLikeFilmUser(Film film, User user) {
            String deleteFilmSql = "DELETE FROM film_like WHERE user_id = ? AND film_id = ?";
            jdbcTemplate.update(deleteFilmSql, user.getId(), film.getId());
            film.getLike().remove(user.getId());
            log.info("Пользователь {} удалил лайк фильму {}", user.getId(), film.getId());
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

    @Override
    public Set<Long> getSetLikesFilmById(Long id) {
        String getLikeSql = "SELECT user_id FROM film_like WHERE film_id = ?";
        List<Long> likeList = jdbcTemplate.queryForList(getLikeSql, Long.class, id);
        return new HashSet<>(likeList);
    }
}
