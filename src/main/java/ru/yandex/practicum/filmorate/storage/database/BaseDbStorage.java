package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseDbStorage<T> implements Storage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T model = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(model);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected void delete(String query, int id) {
        jdbc.update(query, id);
    }

    protected void update(String query, Object... params) {
        if (jdbc.update(query, params) == 0) {
            throw new InternalServerException("Не удалось выполнить обновление");
        }
    }

    protected int create(String query, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return id;
    }
}