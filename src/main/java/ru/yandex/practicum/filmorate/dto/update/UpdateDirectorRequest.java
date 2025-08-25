package ru.yandex.practicum.filmorate.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    @NotNull
    private Integer id;

    @NotNull
    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}