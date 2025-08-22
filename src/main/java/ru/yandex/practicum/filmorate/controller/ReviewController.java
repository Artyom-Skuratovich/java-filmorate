package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    public ReviewDto create(@Valid @RequestBody CreateReviewRequest request) {
        log.info("POST-запрос на создание отзыва");
        ReviewDto created = service.create(request);
        log.info("Отзыв успешно создан, id={}", created.getReviewId());
        return created;
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewRequest request) {
        log.info("PUT-запрос на обновление отзыва, id={}", request.getReviewId());
        ReviewDto updated = service.update(request);
        log.info("Отзыв с id={} успешно обновлён", updated.getReviewId());
        return updated;
    }

    @GetMapping("/{reviewId:\\d+}")
    public ReviewDto find(@PathVariable int reviewId) {
        log.info("GET-запрос на получение отзыва, id={}", reviewId);
        return service.find(reviewId);
    }

    @DeleteMapping("/{reviewId:\\d+}")
    public void delete(@PathVariable int reviewId) {
        log.info("DELETE-запрос на удаление отзыва, id={}", reviewId);
        service.delete(reviewId);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public ReviewDto addLike(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("PUT-запрос на добавление лайка отзыву, reviewId={}, userId={}", reviewId, userId);
        return service.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public ReviewDto addDislike(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("PUT-запрос на добавление дизлайку отзыву, reviewId={}, userId={}", reviewId, userId);
        return service.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public ReviewDto deleteLike(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("DELETE-запрос на удаление лайка отзыву, reviewId={}, userId={}", reviewId, userId);
        return service.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public ReviewDto deleteDislike(@PathVariable int reviewId, @PathVariable int userId) {
        log.info("DELETE-запрос на удаление дизлайка отзыву, reviewId={}, userId={}", reviewId, userId);
        return service.deleteDislike(reviewId, userId);
    }

    @GetMapping(params = "!filmId")
    public List<ReviewDto> getAll(@RequestParam(defaultValue = "10") @Positive Integer count) {
        log.info("GET-запрос на получение списка всех отзывов count={}", count);
        return service.getAllFilmsReviews(count);
    }

    @GetMapping(params = "filmId")
    public List<ReviewDto> getByFilm(@RequestParam Integer filmId,
                                     @RequestParam(defaultValue = "10") @Positive Integer count) {

        log.info("GET-запрос на получение списка отзывов к фильму filmId={}, count={}", filmId, count);
        return service.getSingleFilmReviews(filmId, count);
    }

}