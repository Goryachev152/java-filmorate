package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeFilmStorage likeFilmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmId(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("количество фильмов", count.toString());
        }
        return likeFilmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film updateFilm) {
        validNotFoundFilm(updateFilm.getId());
        return filmStorage.updateFilm(updateFilm);
    }

    public void addLikeFilmUser(Long idFilm, Long idUser) {
        validNotFoundFilm(idFilm);
        validNotFoundUser(idUser);
        likeFilmStorage.addLikeFilmUser(idFilm, idUser);
    }

    public void deleteLikeFilmUser(Long idFilm, Long idUser) {
        Film film = validNotFoundFilm(idFilm);
        User user = validNotFoundUser(idUser);
        likeFilmStorage.deleteLikeFilmUser(film, user);
    }

    private Film validNotFoundFilm(Long id) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilmId(id));
        if (film.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + id + " не найден");
        } else {
            return film.get();
        }
    }

    private User validNotFoundUser(Long id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUserId(id));
        if (user.isEmpty()) {
            throw new NotFoundException("Пользоваетль " + id + " не найден");
        } else {
            return user.get();
        }
    }
}
