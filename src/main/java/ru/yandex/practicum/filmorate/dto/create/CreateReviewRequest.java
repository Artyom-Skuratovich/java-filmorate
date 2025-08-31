package ru.yandex.practicum.filmorate.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotBlank(message = "Отзыв не может быть пустым")
    @Size(max = 500, message = "Длина отзыва не более 500 символов")
    private String content;

    @NotNull(message = "Нужно указать, положительный ли отзыв")
    private Boolean isPositive;

    @NotNull(message = "Должен быть id пользователя создавшего отзыв")
    private Integer userId;

    @NotNull(message = "Должен быть id фильма к которому принадлежит отзыв")
    private Integer filmId;
}