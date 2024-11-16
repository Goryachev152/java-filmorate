package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidateDataCheck implements ConstraintValidator<DataCheck, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate firstDate = LocalDate.of(1895, 12, 28);
        return localDate.isAfter(firstDate) || localDate.isEqual(firstDate);
    }
}
