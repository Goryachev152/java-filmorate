package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getFilms();

    public List<Film> getPopularFilms(Integer count);

    Film createFilm(Film film);

    Film updateFilm(Film updateFilm);

    Optional<Film> findById(Integer id);

    Film getFilmId(Integer id);

    Film likeFilmUser(Integer idFilm, Integer idUser);

    Film deleteLikeFilmUser(Integer idFilm, Integer idUser);
}
