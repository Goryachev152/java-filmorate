package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeFilmStorage {

    void addLikeFilmUser(Long idFilm, Long idUser);

    void deleteLikeFilmUser(Long idFilm, Long idUser);

    List<Film> getPopularFilms(Integer count);
}
