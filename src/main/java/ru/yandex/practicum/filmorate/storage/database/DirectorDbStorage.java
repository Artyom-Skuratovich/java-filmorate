package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String CREATE_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String FIND_DIRECTORS_FOR_FILM_QUERY = """
            SELECT d.*
            FROM directors d
            JOIN films_directors fd ON d.id = fd.director_id
            WHERE fd.film_id = ?
            """;
    private static final String ADD_DIRECTOR_TO_FILM_QUERY = """
            INSERT INTO films_directors (film_id, director_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_DIRECTOR_FROM_FILM_QUERY = """
            DELETE FROM films_directors WHERE film_id = ? AND director_id = ?
            """;

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director create(Director model) {
        model.setId(create(CREATE_QUERY, model.getName()));
        return model;
    }

    @Override
    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Director update(Director model) {
        update(UPDATE_QUERY, model.getName(), model.getId());
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public List<Director> findDirectorsForFilm(int filmId) {
        return findMany(FIND_DIRECTORS_FOR_FILM_QUERY, filmId);
    }

    @Override
    public void addDirectorToFilm(int filmId, int directorId) {
        jdbc.update(ADD_DIRECTOR_TO_FILM_QUERY, filmId, directorId);
    }

    @Override
    public void deleteDirectorFromFilm(int filmId, int directorId) {
        jdbc.update(DELETE_DIRECTOR_FROM_FILM_QUERY, filmId, directorId);
    }
}