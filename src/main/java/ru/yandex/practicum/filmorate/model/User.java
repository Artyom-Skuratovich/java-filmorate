package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @NotNull
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email имеет неверный формат")
    private String email;

    @NotNull
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "День рождения не может быть задан будущим числом")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
}