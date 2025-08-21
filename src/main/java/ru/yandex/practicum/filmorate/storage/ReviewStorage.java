package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage extends Storage<Review> {
    List<Review> findAllFilmsReview(int count);

    List<Review> findFilmReview(int filmId, int count);

    void addLike(int reviewId, int userId);

    boolean deleteLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    boolean deleteDislike(int reviewId, int userId);
}