package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_GENRES_FOR_FILM_QUERY = """
            SELECT g.*
            FROM genres AS g
            INNER JOIN films_genres ON g.id = genre_id
            WHERE film_id = ?
            """;
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE genres SET name = ? WHERE id = ?";
    private static final String CREATE_QUERY = "INSERT INTO genres (name) VALUES (?) RETURNING id";
    private static final String ADD_GENRE_TO_FILM_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRE_FROM_FILM_QUERY = "DELETE FROM films_genres WHERE film_id=? AND genre_id=?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findGenresForFilm(int filmId) {
        return findMany(FIND_GENRES_FOR_FILM_QUERY, filmId);
    }

    @Override
    public void addGenreToFilm(int filmId, int genreId) {
        jdbc.update(ADD_GENRE_TO_FILM_QUERY, filmId, genreId);
    }

    @Override
    public void deleteGenreFromFilm(int filmId, int genreId) {
        jdbc.update(DELETE_GENRE_FROM_FILM_QUERY, filmId, genreId);
    }

    @Override
    public Genre create(Genre model) {
        int id = create(CREATE_QUERY, model.getName());
        model.setId(id);
        return model;
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Genre update(Genre model) {
        update(UPDATE_QUERY, model.getName(), model.getId());
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }
}