package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.yandex.practicum.filmorate.validator.NotSpace;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@EqualsAndHashCode(of = "email")
@Builder(toBuilder = true)
public class User {

    Long id;
    @Email(message = "Email введен некорректно")
    @NotBlank(message = "Email не должен быть пустым")
    String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @NotSpace
    String login;
    String name;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();
}
