package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {

    List<Genre> findAll();

    void addFilmGenre(Long filmId, Film film);

    List<Genre> getListGenreFilmById(Long filmId);
}
