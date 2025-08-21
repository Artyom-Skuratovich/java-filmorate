package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {
    @NotNull
    private Integer reviewId;

    @NotNull(message = "Отзыв не может быть пустым")
    @Size(max = 500, message = "Длина отзыва не более 500 символов")
    private String content;

    @NotNull(message = "Отзыв должен быть либо положительным, либо отрицательным")
    private Boolean isPositive;

    @NotNull(message = "Должен быть id пользователя создавшего отзыв")
    private Integer userId;

    @NotNull(message = "Должен быть id фильма к которому принадлежит отзыв")
    private Integer filmId;
}