package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParameterNotValidException extends IllegalArgumentException {
    String parameter;
    String reason;
}
