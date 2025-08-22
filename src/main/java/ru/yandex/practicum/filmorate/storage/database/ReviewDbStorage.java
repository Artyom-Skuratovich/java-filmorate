package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews";
    private static final String FIND_QUERY = "SELECT * FROM reviews WHERE id = ?";
    private static final String FIND_FILMS_REVIEW_QUERY = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            LIMIT ?;
            """;
    private static final String FIND_ALL_FILMS_REVIEW_QUERY = """
            SELECT *
            FROM reviews
            LIMIT ?;
            """;
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";
    private static final String UPDATE_QUERY = """
            UPDATE reviews
            SET content = ?, positive = ?, user_id = ?, film_id = ?, useful = ?
            WHERE id = ?
            """;
    private static final String CREATE_QUERY = """
            INSERT INTO reviews (content, positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, 0)
            """;
    private static final String ADD_LIKE_QUERY = """
            INSERT INTO review_likes (review_id, user_id) VALUES (?, ?)
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes WHERE review_id = ? AND user_id = ?
            """;
    private static final String ADD_DISLIKE_QUERY = """
            INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?)
            """;
    private static final String DELETE_DISLIKE_QUERY = """
            DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?
            """;

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAllFilmsReview(int count) {
        return findMany(FIND_ALL_FILMS_REVIEW_QUERY, count);
    }

    @Override
    public List<Review> findFilmReview(int filmId, int count) {
        return findMany(FIND_FILMS_REVIEW_QUERY, filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        jdbc.update(ADD_LIKE_QUERY, reviewId, userId);
    }

    @Override
    public boolean deleteLike(int reviewId, int userId) {
        return jdbc.update(DELETE_LIKE_QUERY, reviewId, userId) != 0;
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        jdbc.update(ADD_DISLIKE_QUERY, reviewId, userId);
    }

    @Override
    public boolean deleteDislike(int reviewId, int userId) {
        return jdbc.update(DELETE_DISLIKE_QUERY, reviewId, userId) != 0;
    }

    @Override
    public Review create(Review model) {
        int id = create(
                CREATE_QUERY,
                model.getContent(),
                model.getIsPositive(),
                model.getUserId(),
                model.getFilmId()
        );
        model.setReviewId(id);
        return model;
    }

    @Override
    public List<Review> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Review> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Review update(Review model) {
        update(
                UPDATE_QUERY,
                model.getContent(),
                model.getIsPositive(),
                model.getUserId(),
                model.getFilmId(),
                model.getUseful(),
                model.getReviewId()
        );
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }
}