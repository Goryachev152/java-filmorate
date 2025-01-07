package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface LikeFilmStorage {

    void addLikeFilmUser(Long idFilm, Long idUser);

    void deleteLikeFilmUser(Film film, User user);

    List<Film> getPopularFilms(Integer count);

    Set<Long> getSetLikesFilmById(Long id);
}
