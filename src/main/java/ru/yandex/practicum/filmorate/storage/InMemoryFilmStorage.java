package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> filmById = new HashMap<>();

    public Collection<Film> getFilms() {
        return filmById.values();
    }

    public Film getFilmId(Integer id) {
        return filmById.get(id);
    }

    public Film createFilm(Film film) {
        film = film.toBuilder().id(getNextId()).build();
        filmById.put(film.getId(), film);
        log.info("Фильм с id {} добавлен в сервис", film.getId());
        return film;
    }

    public Film updateFilm(Film updateFilm) {
        Film oldFilm = updateFilm.toBuilder()
                .id(updateFilm.getId())
                .name(updateFilm.getName())
                .description(updateFilm.getDescription())
                .releaseDate(updateFilm.getReleaseDate())
                .duration(updateFilm.getDuration())
                .build();
        filmById.put(updateFilm.getId(), oldFilm);
        log.info("Фильм с id {} обновлен в сервисе", updateFilm.getId());
        return oldFilm;
    }

    public Film likeFilmUser(Integer idFilm, Integer idUser) {
        filmById.get(idFilm).getListLike().add(idUser);
        return filmById.get(idFilm);
    }

    public Film deleteLikeFilmUser(Integer idFilm, Integer idUser) {
        filmById.get(idFilm).getListLike().remove(idUser);
        return filmById.get(idFilm);
    }

    public Optional<Film> findById(Integer id) {
        if (filmById.containsKey(id)) {
            return Optional.of(filmById.get(id));
        } else {
            return Optional.empty();
        }
    }

    private int getNextId() {
        int currentMaxId = filmById.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
