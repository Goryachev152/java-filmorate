package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeFilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeFilmStorage likeFilmStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film getFilmId(Long id) {
        return filmStorage.getFilmId(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count <= 0) {
            throw new ParameterNotValidException("количество фильмов", count.toString());
        }
        return likeFilmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film updateFilm) {
        return filmStorage.updateFilm(updateFilm);
    }

    public void likeFilmUser(Long idFilm, Integer idUser) {
        likeFilmStorage.likeFilmUser(idFilm, idUser);
    }

    public void deleteLikeFilmUser(Long idFilm, Integer idUser) {
        likeFilmStorage.deleteLikeFilmUser(idFilm, idUser);
    }
}
