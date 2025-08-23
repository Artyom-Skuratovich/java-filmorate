package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.EventRowMapper;

import java.util.List;
import java.util.Optional;

// Хранилище событий на базе BaseDbStorage
@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    private static final String INSERT_SQL =
            "INSERT INTO events (ts, user_id, event_type, operation, entity_id) VALUES (?,?,?,?,?)";

    private static final String FIND_BY_USER_SQL =
            "SELECT event_id, ts, user_id, event_type, operation, entity_id " +
                    "FROM events WHERE user_id = ? ORDER BY ts ASC, event_id ASC";

    public EventDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new EventRowMapper());
    }

    @Override
    public Event save(Event event) {
        // PK у events — BIGINT, используем createLong(...)
        long id = createLong(
                INSERT_SQL,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );
        event.setEventId(id);
        return event;
    }

    @Override
    public List<Event> findByUserIdOrderByTimestampAsc(long userId) {
        return findMany(FIND_BY_USER_SQL, userId);
    }

    // ===== Методы из Storage<T>, которые не нужны для Event =====
    @Override
    public Event create(Event model) {
        throw new UnsupportedOperationException("create(Event) not supported, use save()");
    }

    @Override
    public Optional<Event> find(int id) {
        throw new UnsupportedOperationException("find by id not supported for Event");
    }

    @Override
    public List<Event> findAll() {
        throw new UnsupportedOperationException("findAll not supported for Event");
    }

    @Override
    public Event update(Event model) {
        throw new UnsupportedOperationException("update not supported for Event");
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException("delete not supported for Event");
    }
}