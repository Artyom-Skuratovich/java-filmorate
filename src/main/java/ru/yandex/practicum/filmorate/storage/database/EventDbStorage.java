package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    private static final String INSERT_SQL =
            "INSERT INTO events (ts, user_id, event_type, operation, entity_id) VALUES (?,?,?,?,?)";

    private static final String FIND_BY_USER_SQL =
            "SELECT event_id, ts, user_id, event_type, operation, entity_id " +
                    "FROM events WHERE user_id = ? ORDER BY ts, event_id";

    private static final RowMapper<Event> EVENT_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event e = new Event();
            e.setEventId(rs.getLong("event_id"));
            e.setTimestamp(rs.getLong("ts"));
            e.setUserId(rs.getLong("user_id"));
            e.setEventType(EventType.valueOf(rs.getString("event_type")));
            e.setOperation(Operation.valueOf(rs.getString("operation")));
            e.setEntityId(rs.getLong("entity_id"));
            return e;
        }
    };

    public EventDbStorage(JdbcTemplate jdbc) {
        super(jdbc, EVENT_ROW_MAPPER);
    }

    // Storage<T>
    @Override
    public Event create(Event event) {
        long id = createLong(INSERT_SQL,            // <-- usa createLong per PK BIGINT
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
        event.setEventId(id);
        return event;
    }

    @Override
    public Optional<Event> find(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Event update(Event model) {
        throw new UnsupportedOperationException();
    }

    // EventStorage
    @Override
    public Event save(Event event) {
        return create(event);
    }

    @Override
    public List<Event> findByUserIdOrderByTimestampAsc(long userId) {
        return findMany(FIND_BY_USER_SQL, userId);
    }
}