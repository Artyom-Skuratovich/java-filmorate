package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.abstraction.FilmStorage;

import java.util.List;
import java.util.Optional;

public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findMostPopularFilms(int count) {
        return List.of();
    }

    @Override
    public Film create(Film model) {
        return null;
    }

    @Override
    public List<Film> findAll() {
        return List.of();
    }

    @Override
    public Optional<Film> find(int id) {
        return Optional.empty();
    }

    @Override
    public Film update(Film model) {
        return null;
    }

    @Override
    public void delete(int id) {

    }
}