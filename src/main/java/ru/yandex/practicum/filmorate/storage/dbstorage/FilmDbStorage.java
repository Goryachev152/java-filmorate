package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    @Override
    public List<Film> getFilms() {
        final String sqlQuery = "SELECT id, name, description, releaseDate, duration, mpa_id FROM films";
        List<Film> listFilm = jdbcTemplate.query(sqlQuery, filmRowMapper::mapRow);
        return listFilm.stream()
                .map(film -> film.toBuilder()
                             .genres(filmGenreStorage.getListGenreFilmId(film.getId()))
                             .like(getLikeFilmId(film.getId()))
                             .build())
                .toList();
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long filmId;

        final String filmSql = "INSERT INTO films(name, description, releaseDate, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";
        mpaStorage.getCountById(film);
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(filmSql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        if (Objects.nonNull(keyHolder.getKey())) {
            filmId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка добавления фильма в таблицу");
        }
        List<Genre> resultGenres = genreStorage.getExistGenres(film).stream().toList();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.addFilmGenre(filmId, genre.getId(), film);
            }
        }
        return Film.builder()
                .id(filmId)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(resultGenres)
                .build();
    }

    @Override
    public Film updateFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Long filmId;

        String updateFilmSql = "UPDATE Films SET name = ?, description = ?, releaseDate = ?, duration = ?," +
                " mpa_id = ? WHERE id = ?";

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(updateFilmSql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            stmt.setLong(6, film.getId());
            return stmt;
        }, keyHolder);
        if (Objects.nonNull(keyHolder.getKey())) {
            filmId = keyHolder.getKey().longValue();
        } else {
            throw new NotFoundException("Ошибка обновления Фильма");
        }
        List<Genre> resultGenres = genreStorage.getExistGenres(film).stream().toList();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.addFilmGenre(film.getId(), genre.getId(), film);
            }
        }
        Film resultFilm = Film.builder()
                .id(filmId)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(resultGenres)
                .like(getLikeFilmId(filmId))
                .build();
        if (rows > 0) {
            log.info("Фильм с id = {} успешно обновлён", filmId);
            return resultFilm;

        } else {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    @Override
    public Film getFilmId(Long id) {
        String getFilmSql = "SELECT id, name, description, releaseDate, duration, mpa_id " +
                "FROM films WHERE id = ?";

        Optional<Film> resultFilm;
        try {
            resultFilm = Optional.ofNullable(jdbcTemplate.queryForObject(getFilmSql,
                    filmRowMapper::mapRow, id));
        } catch (EmptyResultDataAccessException e) {
            resultFilm = Optional.empty();
        }
        if (resultFilm.isPresent()) {
            return resultFilm.get().toBuilder()
                    .genres(filmGenreStorage.getListGenreFilmId(id))
                    .like(getLikeFilmId(id))
                    .build();

        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    public Set<Integer> getLikeFilmId(Long filmId) {
        String getLikeSql = "SELECT user_id FROM film_like WHERE film_id = ?";
        List<Integer> likeList = jdbcTemplate.queryForList(getLikeSql, Integer.class, filmId);
        return new HashSet<>(likeList);
    }
}
