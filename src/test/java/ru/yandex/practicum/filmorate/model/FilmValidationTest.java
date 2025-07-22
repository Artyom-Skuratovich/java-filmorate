package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
public class FilmValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    public void shouldReturnOneValidationErrorWhenDescriptionSizeIsBiggerThen200Symbols() {
        Film film = new Film();
        film.setDescription("d".repeat(201));
        film.setName("Star Wars");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Длина описания превышает 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenNameIsBlank() {
        Film film = new Film();
        film.setDescription("d".repeat(200));
        film.setName("");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenDurationIsNotPositive() {
        Film film = new Film();
        film.setDescription("d".repeat(200));
        film.setName("escape from Shawshank");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenReleaseDateIsAfterMinReleaseDate() {
        Film film = new Film();
        film.setDescription("d".repeat(200));
        film.setName("escape from Shawshank");
        film.setDuration(1);
        film.setReleaseDate(LocalDate.parse("1867-12-12"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }
}
