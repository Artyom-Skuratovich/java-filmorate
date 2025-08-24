package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.dto.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("reviewDbStorage")
    private final ReviewStorage reviewStorage;

    // сервис ленты — добавляем события REVIEW/ADD|UPDATE|REMOVE
    private final EventService eventService;

    public List<ReviewDto> findAll() {
        return reviewStorage.findAll()
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public ReviewDto find(int reviewId) {
        Review review = StorageUtils.findModel(
                reviewStorage,
                reviewId,
                String.format("Отзыв с id=%d не найден", reviewId)
        );
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto create(CreateReviewRequest request) {
        // валидации существования фильма и пользователя
        Film film = StorageUtils.findModel(
                filmStorage, request.getFilmId(),
                String.format("Отзыв для фильма id=%d не создан. Такого фильма нет.", request.getFilmId())
        );
        User user = StorageUtils.findModel(
                userStorage, request.getUserId(),
                String.format("Не удалось обновить отзыв, так как пользователя с id=%d не существует", request.getUserId())
        );

        Review review = reviewStorage.create(ReviewMapper.mapToReview(request));

        // событие — отзыв создан
        eventService.addEvent(user.getId(), EventType.REVIEW, Operation.ADD, review.getReviewId());

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto update(UpdateReviewRequest request) {
        Review review = StorageUtils.findModel(
                reviewStorage, request.getReviewId(),
                String.format("Отзыв с id=%d не найден", request.getReviewId())
        );
        User user = StorageUtils.findModel(
                userStorage, request.getUserId(),
                String.format("Не удалось обновить отзыв, так как пользователя с id=%d не существует", request.getUserId())
        );
        if (request.getUserId() != user.getId()) {
            throw new NotFoundException(String.format(
                    "Не удалось обновить отзыв так как пользователь с id=%d не автор отзыва",
                    request.getUserId()
            ));
        }

        review = reviewStorage.update(ReviewMapper.updateReviewProperties(review, request));

        // событие — отзыв обновлён
        eventService.addEvent(user.getId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());

        return ReviewMapper.mapToReviewDto(review);
    }

    public void delete(int reviewId) {
        // достаём отзыв, чтобы знать userId и корректно записать событие
        Review review = StorageUtils.findModel(
                reviewStorage, reviewId,
                String.format("Отзыв с id=%d не найден", reviewId)
        );
        reviewStorage.delete(reviewId);

        // событие — отзыв удалён
        eventService.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId);
    }

    public ReviewDto addLike(int reviewId, int userId) {
        Review review = StorageUtils.findModel(
                reviewStorage, reviewId,
                String.format("Не удалось поставить лайк отзыву с id=%d, так как такого отзыва не существует", reviewId)
        );
        User user = StorageUtils.findModel(
                userStorage, userId,
                String.format("Не удалось поставить лайк отзыву, так как пользователя с id=%d не существует", userId)
        );
        if (reviewStorage.deleteDislike(reviewId, userId)) {
            review.setUseful(review.getUseful() + 1);
        }
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
        reviewStorage.addLike(review.getReviewId(), user.getId());
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto deleteLike(int reviewId, int userId) {
        Review review = StorageUtils.findModel(
                reviewStorage, reviewId,
                String.format("Не удалось удалить лайк отзыву с id=%d, так как такого отзыва не существует", reviewId)
        );
        User user = StorageUtils.findModel(
                userStorage, userId,
                String.format("Не удалось удалить лайк отзыву, так как пользователя с id=%d не существует", userId)
        );
        if (!reviewStorage.deleteLike(reviewId, userId)) {
            throw new NotFoundException(String.format(
                    "Не удалось удалить лайк отзыву с id=%d, так как пользователь с id=%d не ставил лайк",
                    reviewId, userId
            ));
        }
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto addDislike(int reviewId, int userId) {
        Review review = StorageUtils.findModel(
                reviewStorage, reviewId,
                String.format("Не удалось поставить дизлайк отзыву с id=%d, так как такого отзыва не существует", reviewId)
        );
        User user = StorageUtils.findModel(
                userStorage, userId,
                String.format("Не удалось поставить дизлайк отзыву, так как пользователя с id=%d не существует", userId)
        );
        if (reviewStorage.deleteLike(reviewId, userId)) {
            review.setUseful(review.getUseful() - 1);
        }
        review.setUseful(review.getUseful() - 1);
        reviewStorage.update(review);
        reviewStorage.addDislike(review.getReviewId(), user.getId());
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto deleteDislike(int reviewId, int userId) {
        Review review = StorageUtils.findModel(
                reviewStorage, reviewId,
                String.format("Не удалось удалить дизлайк отзыву с id=%d, так как такого отзыва не существует", reviewId)
        );
        User user = StorageUtils.findModel(
                userStorage, userId,
                String.format("Не удалось удалить дизлайк отзыву, так как пользователя с id=%d не существует", userId)
        );
        if (!reviewStorage.deleteDislike(reviewId, userId)) {
            throw new NotFoundException(String.format(
                    "Не удалось удалить дизлайк отзыву с id=%d, так как пользователь с id=%d не ставил лайк",
                    reviewId, userId
            ));
        }
        review.setUseful(review.getUseful() + 1);
        reviewStorage.update(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getAllFilmsReviews(int count) {
        return reviewStorage.findAllFilmsReview(count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }

    public List<ReviewDto> getSingleFilmReviews(int filmId, int count) {
        return reviewStorage.findFilmReview(filmId, count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }
}