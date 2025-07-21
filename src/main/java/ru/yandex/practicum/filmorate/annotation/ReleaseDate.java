package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {
    String message();

    String value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}