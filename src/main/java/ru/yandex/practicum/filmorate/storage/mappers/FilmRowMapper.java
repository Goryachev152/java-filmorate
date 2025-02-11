package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeFilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeFilmStorage  likeFilmStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long mpaId = rs.getLong("mpa_id");
        Mpa mpa = mpaStorage.findById(mpaId);
        List<Genre> result = filmGenreStorage.getListGenreFilmById(rs.getLong("id"));
        Set<Long> like = likeFilmStorage.getSetLikesFilmById(rs.getLong("id"));
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("releaseDate")))
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(result)
                .like(like)
                .build();
    }
}
