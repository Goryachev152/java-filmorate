package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.yandex.practicum.filmorate.validator.DataCheck;

import java.time.LocalDate;

/**
 * Film.
 */
@Value
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
public class Film {

    Integer id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    String description;
    @NotNull(message = "Дата указана некорректо или не указана вообще")
    @DataCheck
    LocalDate releaseDate;
    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;
}
