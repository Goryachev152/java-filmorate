package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getAllGenre() {
        String getAllGenreSql = "SELECT * FROM genre";
        List<Genre> listGenre = jdbcTemplate.query(getAllGenreSql, genreRowMapper::mapRow);
        return listGenre;
    }

    @Override
    public Genre getGenreId(Long id) {
        String genreIdSql = "SELECT * FROM genre WHERE id = ?";
        Optional<Genre> genre;
        try {
            genre = Optional.ofNullable(jdbcTemplate.queryForObject(genreIdSql, genreRowMapper::mapRow, id));
        } catch (EmptyResultDataAccessException e) {
            genre = Optional.empty();
        }
        if (genre.isPresent()) {
            return genre.get();
        } else {
            throw new NotFoundException("Жанр " + id + " не найден");
        }
    }

    public List<Genre> getExistGenres(Film film) {
        String sqlQuery = "SELECT id FROM genre";
        List<Long> genres = jdbcTemplate.queryForList(sqlQuery, Long.class);
        List<Genre> filmGenres = film.getGenres();
        List<Genre> resultGenres = new ArrayList<>();

        if (Objects.nonNull(filmGenres)) {
            filmGenres.forEach(genre -> {
                        if (genres.contains(genre.getId())) {
                            resultGenres.add(genre);
                        } else {
                            throw new ValidationException("Указанный жанр не существует");
                        }
                    }
            );
        }
        return resultGenres;
    }
}
