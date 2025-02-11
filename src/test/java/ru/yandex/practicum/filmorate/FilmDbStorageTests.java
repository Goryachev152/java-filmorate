package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.LikeFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaDbStorage.class,
        FilmGenreDbStorage.class, GenreDbStorage.class, GenreRowMapper.class, LikeFilmDbStorage.class})
class FilmDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @AfterEach
    void removeFilmsTable() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
    }

    @Test
    public void testGetFilms() {
        Optional<List<Film>> filmListOptional = Optional.of(filmStorage.getFilms());

        AssertionsForClassTypes.assertThat(filmListOptional.get().size())
                .isEqualTo(3);
    }

    @Test
    public void testGetFilmId() {
        Optional<Film> filmOptional = Optional.of(filmStorage.getFilmId(1L));

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "film1")
                                .hasFieldOrPropertyWithValue("description", "description1")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2005-08-24"))
                                .hasFieldOrPropertyWithValue("duration", 200)
                );
    }

    @Test
    public void testCreateFilm() {
        Mpa mpa = Mpa.builder()
                .id(4L)
                .name("R")
                .build();
        Film testFilm = Film.builder()
                .name("film4")
                .description("description4")
                .releaseDate(LocalDate.of(2003,05,12))
                .duration(200)
                .mpa(mpa)
                .build();
        filmStorage.createFilm(testFilm);
        Optional<Film> filmOptional = Optional.of(filmStorage.getFilmId(4L));

        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("name", "film4")
                                .hasFieldOrPropertyWithValue("description", "description4")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2003-05-12"))
                                .hasFieldOrPropertyWithValue("duration", 200)
                );
    }

    @Test
    public void testUpdateFilm() {
        Mpa mpa = Mpa.builder()
                .id(4L)
                .name("R")
                .build();
        Film testFilm = Film.builder()
                .id(1L)
                .name("film5")
                .description("description5")
                .releaseDate(LocalDate.of(1980, 06, 29))
                .duration(160)
                .mpa(mpa)
                .build();
        Optional<Film> filmOptional = Optional.of(filmStorage.updateFilm(testFilm));
        AssertionsForClassTypes.assertThat(filmOptional)
                .isPresent()
                .isEqualTo(Optional.of(testFilm));
    }
}
