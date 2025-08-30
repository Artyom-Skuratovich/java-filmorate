package ru.yandex.practicum.filmorate.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDirectorRequest {
    @NotNull
    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}