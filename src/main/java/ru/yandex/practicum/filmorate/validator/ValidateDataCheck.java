package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidateDataCheck implements ConstraintValidator<DataCheck, LocalDate> {

    static final LocalDate FIRST_DATE = LocalDate.of(1895, 12, 28);
    static final String MESSAGE = "дата релиза должна быть не раньше 28 декабря 1895 года";

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {

        return localDate.isAfter(FIRST_DATE) || localDate.isEqual(FIRST_DATE);
    }
}
