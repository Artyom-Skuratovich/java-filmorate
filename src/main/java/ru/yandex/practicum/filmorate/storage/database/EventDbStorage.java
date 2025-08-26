package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {
    private static final String FIND_FOR_USER_QUERY = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id;";
    private static final String CREATE_QUERY = """
            INSERT INTO events (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Event> findForUser(int userId) {
        return findMany(FIND_FOR_USER_QUERY, userId);
    }

    @Override
    public Event create(Event model) {
        model.setEventId(create(
                CREATE_QUERY,
                model.getTimestamp(),
                model.getUserId(),
                model.getEventType().name(),
                model.getOperation().name(),
                model.getEntityId()
        ));
        return model;
    }

    @Override
    public List<Event> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Event> find(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Event update(Event model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }
}