package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeFilmStorage {

    void likeFilmUser(Long idFilm, Integer idUser);

    void deleteLikeFilmUser(Long idFilm, Integer idUser);

    List<Film> getPopularFilms(Integer count);
}
