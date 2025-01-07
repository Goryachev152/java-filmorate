package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public void likeFilmUser(Long idFilm, Integer idUser) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilmId(idFilm));
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(idUser));
        if (user.isPresent() && film.isPresent()) {
            String checkSql = "SELECT film_id FROM film_like WHERE user_id = ?";
            Optional<Integer> checkId;
            try {
                checkId =
                        Optional.ofNullable(jdbcTemplate.queryForObject(checkSql, Integer.class, idUser));
                throw new NotFoundException("Пользователь " + idUser + " уже поставил лайк фильму " + idFilm);
            } catch (EmptyResultDataAccessException e) {
                checkId = Optional.empty();
            }
        String addLikeSql = "INSERT INTO film_like(user_id, film_id) values (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(addLikeSql);
            stmt.setInt(1, idUser);
            stmt.setLong(2, idFilm);
            return stmt;
        });
        film.get().getLike().add(idUser);
        log.info("Пользователь {} поставил лайк фильму {}", idUser, idFilm);

    } else if (user.isEmpty()) {
        throw new NotFoundException("Пользоваетль " + idUser + " не найден");
    } else if (film.isEmpty()) {
        throw new NotFoundException("Фильм " + idFilm + " не найден");
    }
    }

    @Override
    public void deleteLikeFilmUser(Long idFilm, Integer idUser) {
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
