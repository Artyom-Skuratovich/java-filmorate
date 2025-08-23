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
    private static final String FIND_MOST_POPULAR_FILMS_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN likes AS l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.film_id) DESC
            LIMIT ?;
            """;
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

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findMostPopularFilms(int count) {
        return findMany(FIND_MOST_POPULAR_FILMS_QUERY, count);
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
        // удаляем фильм; каскады почистят likes и films_genres
        delete(DELETE_QUERY, id);   // <-- helper из BaseDbStorage
    }

    @Override
    public List<Film> findFilmRecommendations(int userId) {
        // временная заглушка для рекомендаций фильмов, пока логика не реализована
        return List.of();
    }

}