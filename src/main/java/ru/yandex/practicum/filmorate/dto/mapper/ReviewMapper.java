package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.create.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

public final class ReviewMapper {
    private ReviewMapper() {
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());

        return dto;
    }

    public static Review mapToReview(CreateReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());

        return review;
    }

    public static Review updateReviewProperties(Review review, UpdateReviewRequest request) {
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        return review;
    }
}