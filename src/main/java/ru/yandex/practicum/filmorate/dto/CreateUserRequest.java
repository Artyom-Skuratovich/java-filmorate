package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserRequest {
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
}