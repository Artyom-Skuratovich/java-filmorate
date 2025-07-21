package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private LocalDate minReleaseDate;

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        minReleaseDate = LocalDate.parse(constraintAnnotation.value());
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !minReleaseDate.isAfter(localDate);
    }
}