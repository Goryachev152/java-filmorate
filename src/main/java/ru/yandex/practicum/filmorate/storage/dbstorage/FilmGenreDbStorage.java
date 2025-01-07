package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final GenreRowMapper genreRowMapper;

    @Override
    public void addFilmGenre(Long filmId, Long genreId, Film film) {
        List<Genre> resultGenres = genreStorage.getExistGenres(film).stream().toList();

        final String sqlQueryFilmGenres = "INSERT INTO genre_films(film_id, genre_id) " +
                "values (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryFilmGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setLong(1, filmId);
                preparedStatement.setLong(2, resultGenres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return resultGenres.size();
            }
        });
    }

    @Override
    public List<Genre> findAll() {
        final String sqlQuery = "SELECT * FROM film_genre";
        return jdbcTemplate.query(sqlQuery, genreRowMapper::mapRow);
    }

    @Override
    public List<Genre> getListGenreFilmId(Long filmId) {
        List<Genre> result = new ArrayList<>();

        String filmGenresQuery = "SELECT genre_id, " +
                "FROM genre_films " +
                "WHERE film_id = ? ";

        List<Long> genreIds = jdbcTemplate.queryForList(filmGenresQuery, Long.class, filmId);
        List<Genre> genres = genreStorage.getAllGenre().stream().toList();

        for (Genre genre : genres) {
            if (genreIds.contains(genre.getId())) {
                result.add(genre);
            }
        }

        return result;
    }
}
