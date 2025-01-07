package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa findById(Long id) {
        Optional<Mpa> resultMpa;
        String sqlQuery = "SELECT id, name " +
                "FROM mpa WHERE id = ?";

        try {
            resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            resultMpa = Optional.empty();
        }

        return resultMpa.orElse(null);
    }

    @Override
    public Mpa getNameById(Long id) {
        String sqlQuery = "SELECT * " +
                "FROM mpa WHERE id = ?";

        Optional<Mpa> resultMpa;

        try {
            resultMpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            resultMpa = Optional.empty();
        }

        if (resultMpa.isPresent()) {
            return resultMpa.get();

        } else {
            throw new NotFoundException("Mpa с id = " + id + " не найден");
        }
    }

    @Override
    public Long getCountById(Film film) {
        Long count;
        final String sqlQueryMpa = "SELECT COUNT(*) " +
                "FROM mpa WHERE id = ?";

        try {
            count = jdbcTemplate.queryForObject(sqlQueryMpa, Long.class, film.getMpa().getId());
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("MPA id не существуют");
        }

        if (Objects.isNull(count) || count == 0) {
            throw new ValidationException("MPA id не существует");
        }

        return count;
    }

    public Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
