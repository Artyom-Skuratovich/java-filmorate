package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;
    private static final String CREATE_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_FILM_RECOMMENDATIONS_QUERY = """
            WITH user_likes AS (
              SELECT film_id
              FROM likes
              WHERE user_id = ?
            ),
            overlaps AS (
              SELECT l2.user_id AS other_user, COUNT(*) AS common_cnt
              FROM likes l1
              JOIN likes l2 ON l1.film_id = l2.film_id
              WHERE l1.user_id = ? AND l2.user_id <> ?
              GROUP BY l2.user_id
            ),
            best AS (
              SELECT other_user
              FROM overlaps
              WHERE common_cnt = (SELECT COALESCE(MAX(common_cnt), 0) FROM overlaps)
            ),
            candidates AS (
              SELECT l.film_id
              FROM likes l
              JOIN best b ON b.other_user = l.user_id
            )
            SELECT f.*, COUNT(*) AS votes
            FROM films f
            JOIN candidates c ON c.film_id = f.id
            LEFT JOIN user_likes ul ON ul.film_id = f.id
            WHERE ul.film_id IS NULL
            GROUP BY f.id
            ORDER BY votes DESC, f.id ASC;
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findMostPopularFilms(int count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            return findMany("""
            SELECT f.*
            FROM films f
            JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """, genreId, year, count);
        } else if (genreId != null) {
            return findMany("""
            SELECT f.*
            FROM films f
            JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE fg.genre_id = ?
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """, genreId, count);
        } else if (year != null) {
            return findMany("""
            SELECT f.*
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE EXTRACT(YEAR FROM f.release_date) = ?
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """, year, count);
        } else {
            return findMany("""
            SELECT f.*
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """, count);
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        return jdbc.update(DELETE_LIKE_QUERY, filmId, userId) != 0;
    }

    @Override
    public Film create(Film model) {
        int id = create(
                CREATE_QUERY,
                model.getName(),
                model.getDescription(),
                model.getReleaseDate(),
                model.getDuration(),
                model.getMpaId()
        );
        model.setId(id);
        return model;
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Film update(Film model) {
        update(
                UPDATE_QUERY,
                model.getName(),
                model.getDescription(),
                model.getReleaseDate(),
                model.getDuration(),
                model.getMpaId(),
                model.getId()
        );
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> findFilmRecommendations(int userId) {
        return findMany(FIND_FILM_RECOMMENDATIONS_QUERY, userId, userId, userId);
    }
}