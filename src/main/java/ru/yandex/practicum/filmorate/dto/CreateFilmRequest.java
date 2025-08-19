package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateFilmRequest {
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
    private Integer duration;

    @NotNull
    private Mpa mpa;

    private Set<Genre> genres;
}