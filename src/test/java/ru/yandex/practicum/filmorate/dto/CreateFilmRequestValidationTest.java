package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.dto.create.CreateFilmRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CreateFilmRequestValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;
    private Mpa mpa;

    @BeforeEach
    public void setUp() {
        mpa = new Mpa();
        mpa.setName("PG-13");
        mpa.setId(new Random().nextInt());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenDescriptionSizeIsBiggerThen200Symbols() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setDescription("d".repeat(201));
        request.setName("Star Wars");
        request.setDuration(1);
        request.setReleaseDate(LocalDate.now());
        request.setMpa(mpa);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Длина описания превышает 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenNameIsBlank() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setDescription("d".repeat(200));
        request.setName("");
        request.setDuration(1);
        request.setReleaseDate(LocalDate.now());
        request.setMpa(mpa);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenDurationIsNotPositive() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setDescription("d".repeat(200));
        request.setName("escape from Shawshank");
        request.setDuration(0);
        request.setReleaseDate(LocalDate.now());
        request.setMpa(mpa);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenReleaseDateIsAfterMinReleaseDate() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setDescription("d".repeat(200));
        request.setName("escape from Shawshank");
        request.setDuration(1);
        request.setReleaseDate(LocalDate.parse("1867-12-12"));
        request.setMpa(mpa);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }
}