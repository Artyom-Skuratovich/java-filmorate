package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String FIND_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Mpa create(Mpa model) {
        return null;
    }

    @Override
    public List<Mpa> findAll() {
        return List.of();
    }

    @Override
    public Optional<Mpa> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Mpa update(Mpa model) {
        return null;
    }

    @Override
    public void delete(int id) {

    }
}