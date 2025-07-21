package ru.yandex.practicum.filmorate.model;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
public class UserValidationTest {
    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    public void shouldReturnOneValidationErrorWhenLoginHasWhitespaces() {
        User user = new User();
        user.setName("Default name");
        user.setLogin("Incorrect login");
        user.setEmail("default@gmail.com");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnTwoValidationErrorsWhenLoginIsBlank() {
        User user = new User();
        user.setName("Default name");
        user.setLogin("");
        user.setEmail("default@gmail.com");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Логин не может быть пустым")));
    }

    @Test
    public void shouldReturnOneValidationErrorWhenEmailIsIncorrect() {
        User user = new User();
        user.setName("Default name");
        user.setLogin("overlord");
        user.setEmail("default_gmail.com");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("Email имеет неверный формат", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldReturnOneValidationErrorWhenBirthdayIsInTheFuture() {
        User user = new User();
        user.setName("Default name");
        user.setLogin("overlord");
        user.setEmail("default@gmail.com");
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("День рождения не может быть задан будущим числом", violations.iterator().next().getMessage());
    }
}