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
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class UserControllerTest {
    User user;
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
        uri = URI.create("http://localhost:8080/users");
        user = User.builder()
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1998, 7, 25))
                .email("gorych15300@yandex.ru")
                .build();
        ctx = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    public void stop() {
        ctx.close();
    }

    @Test
    public void createUser() throws IOException, InterruptedException {
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Пользователь добавлен в сервис");
    }

    @Test
    public void notBlankEmail() throws IOException, InterruptedException {
        user = user.toBuilder().email("").build();
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Почта не должна быть пустой");
    }

    @Test
    public void emailContainsSpecialSign() throws IOException, InterruptedException {
        user = user.toBuilder().email("gorych152.ru").build();
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Почта должна содержать знак @");
    }

    @Test
    public void notSpaceLogin() throws IOException, InterruptedException {
        user = user.toBuilder().login("gorych 152").build();
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Логин не должен содержать пробелов");
    }

    @Test
    public void notBlankLogin() throws IOException, InterruptedException {
        user = user.toBuilder().login("").build();
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Логин не должен быть пустым");
    }

    @Test
    public void birthdayNotBeforeFuture() throws IOException, InterruptedException {
        user = user.toBuilder().birthday(LocalDate.of(2030, 5, 25)).build();
        String filmJson = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode(), "Дата рождения не может быть в будущем");
    }
}
