package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Integer, Film> filmMap = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film = film.toBuilder().id(getNextId()).build();
        filmMap.put(film.getId(), film);
        log.info("Фильм с id {} добавлен в сервис", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm) {
        if (!filmMap.containsKey(updateFilm.getId())) {
            log.error("Фильм с id {} не найден", updateFilm.getId());
            throw new ValidationException("Фильм с таким id = " + updateFilm.getId() + " не найден");
        }
        Film oldFilm = updateFilm.toBuilder()
                .id(updateFilm.getId())
                .name(updateFilm.getName())
                .description(updateFilm.getDescription())
                .releaseDate(updateFilm.getReleaseDate())
                .duration(updateFilm.getDuration())
                .build();
        filmMap.put(updateFilm.getId(), oldFilm);
        log.info("Фильм с id {} обновлен в сервисе", updateFilm.getId());
        return oldFilm;
    }

    private int getNextId() {
        int currentMaxId = filmMap.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
