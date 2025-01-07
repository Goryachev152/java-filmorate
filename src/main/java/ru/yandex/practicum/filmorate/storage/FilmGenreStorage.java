package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {

    List<Genre> findAll();

    void addFilmGenre(Long filmId, Long genreId, Film film);

    List<Genre> getListGenreFilmId(Long filmId);
}
