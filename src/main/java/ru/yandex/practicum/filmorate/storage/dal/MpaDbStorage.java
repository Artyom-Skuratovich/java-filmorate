package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.abstraction.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<MpaRating> implements MpaStorage {
    private static final String FIND_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public MpaRating create(MpaRating model) {
        return null;
    }

    @Override
    public List<MpaRating> findAll() {
        return List.of();
    }

    @Override
    public Optional<MpaRating> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public MpaRating update(MpaRating model) {
        return null;
    }

    @Override
    public void delete(int id) {

    }
}