package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilmId(Integer id) {
        if (filmStorage.findById(id).isEmpty()) {
            log.error("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм с таким id = " + id + " не найден");
        }
        return filmStorage.getFilmId(id);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("количество фильмов", count.toString());
        }
        Comparator<Film> filmComparator = Comparator.comparingInt(film -> film.getListLike().size());
        return getFilms()
                .stream()
                .sorted(filmComparator.reversed())
                .limit(count)
                .toList();
    }

    public Film updateFilm(Film updateFilm) {
        if (filmStorage.findById(updateFilm.getId()).isEmpty()) {
            log.error("Фильм с id {} не найден", updateFilm.getId());
            throw new NotFoundException("Фильм с таким id = " + updateFilm.getId() + " не найден");
        }
        return filmStorage.updateFilm(updateFilm);
    }

    public Film likeFilmUser(Integer idFilm, Integer idUser) {
        if (filmStorage.findById(idFilm).isEmpty()) {
            log.error("Фильм с id {} не найден", idFilm);
            throw new NotFoundException("Фильм с таким id = " + idFilm + " не найден");
        }
        if (userStorage.findById(idUser).isEmpty()) {
            log.error("Пользователь с id {} не найден", idUser);
            throw new NotFoundException("Пользователь с таким id = " + idUser + " не найден");
        }
        return filmStorage.likeFilmUser(idFilm, idUser);
    }

    public Film deleteLikeFilmUser(Integer idFilm, Integer idUser) {
        if (filmStorage.findById(idFilm).isEmpty()) {
            log.error("Фильм с id {} не найден", idFilm);
            throw new NotFoundException("Фильм с таким id = " + idFilm + " не найден");
        }
        if (userStorage.findById(idUser).isEmpty()) {
            log.error("Пользователь с id {} не найден", idUser);
            throw new NotFoundException("Пользователь с таким id = " + idUser + " не найден");
        }
        return filmStorage.deleteLikeFilmUser(idFilm, idUser);
    }
}
