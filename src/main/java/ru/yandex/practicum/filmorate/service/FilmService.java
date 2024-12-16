package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilmId(Integer id) {
       validNotFoundFilm(id);
        return filmStorage.getFilmId(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("количество фильмов", count.toString());
        }
        return filmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film updateFilm) {
       validNotFoundFilm(updateFilm.getId());
        return filmStorage.updateFilm(updateFilm);
    }

    public Film likeFilmUser(Integer idFilm, Integer idUser) {
        validNotFoundFilm(idFilm);
        validNotFoundUser(idUser);
        return filmStorage.likeFilmUser(idFilm, idUser);
    }

    public Film deleteLikeFilmUser(Integer idFilm, Integer idUser) {
        validNotFoundFilm(idFilm);
        validNotFoundUser(idUser);
        return filmStorage.deleteLikeFilmUser(idFilm, idUser);
    }

    private void validNotFoundUser(Integer idUser) {
        if (userStorage.findById(idUser).isEmpty()) {
            throw new NotFoundException("Пользователь с таким id = " + idUser + " не найден");
        }
    }

    private void validNotFoundFilm(Integer idFilm) {
        if (filmStorage.findById(idFilm).isEmpty()) {
            throw new NotFoundException("Фильм с таким id = " + idFilm + " не найден");
        }
    }
}
