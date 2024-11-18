package ru.yandex.practicum.filmorate;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class FilmControllerTest {

    FilmController filmController;
    Film film;
    HttpClient httpClient;
    Gson gson;
    URI uri;
    ConfigurableApplicationContext ctx;

    @BeforeEach
    public void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        httpClient = HttpClient.newHttpClient();
        uri = URI.create("http://localhost:8080/films");
        filmController = new FilmController();
        film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1998, 7, 25))
                .duration(60)
                .build();
        ctx = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    public void stop() {
        ctx.close();
    }

    @Test
    public void createFilmTest() throws IOException, InterruptedException {
        String filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Фильм не добавлен в сервис");
    }

    @Test
    public void notBlankNameFilm() throws IOException, InterruptedException {
        film = film.toBuilder().name("").build();
        String filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Название не может быть пустым");
    }

    @Test
    public void dateCheckTest() throws IOException, InterruptedException {
        film = film.toBuilder().releaseDate(LocalDate.of(1894, 6, 25)).build();
        String filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(),
                "Дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    public void durationFilmNegative() throws IOException, InterruptedException {
        film = film.toBuilder().duration(-100).build();
        String filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(),
                "Продолжительность фильма должна быть положительным числом");
    }

    @Test
    public void descriptionFilmMaxSize200() throws IOException, InterruptedException {
        film = film.toBuilder().description("b".repeat(201)).build();
        String filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(),
                "Описание фильма не должно быть больше 200 символов");
    }
}
