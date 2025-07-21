package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotNull
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Длина описания превышает 200 символов")
    private String description;

    @NotNull
    @ReleaseDate(value = "1895-12-28", message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;
}