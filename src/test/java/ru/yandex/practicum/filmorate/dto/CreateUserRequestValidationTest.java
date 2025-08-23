package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.dto.create.CreateUserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CreateUserRequestValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    public void shouldReturnOneValidationErrorWhenLoginHasWhitespaces() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Default name");
        request.setLogin("Incorrect login");
        request.setEmail("default@gmail.com");
        request.setBirthday(LocalDate.now());

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnTwoValidationErrorsWhenLoginIsBlank() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Default name");
        request.setLogin("");
        request.setEmail("default@gmail.com");
        request.setBirthday(LocalDate.now());

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Логин не может быть пустым")));
    }

    @Test
    public void shouldReturnOneValidationErrorWhenEmailIsIncorrect() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Default name");
        request.setLogin("overlord");
        request.setEmail("default_gmail.com");
        request.setBirthday(LocalDate.now());

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Email имеет неверный формат", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenBirthdayIsInTheFuture() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Default name");
        request.setLogin("overlord");
        request.setEmail("default@gmail.com");
        request.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("День рождения не может быть задан будущим числом", violations.iterator().next().getMessage());
    }
}