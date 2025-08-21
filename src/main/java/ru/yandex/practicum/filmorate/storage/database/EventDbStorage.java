package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.EventRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

// Хранилище событий на базе JdbcTemplate
@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbc;
    private final EventRowMapper mapper = new EventRowMapper();

    @Override
    public Event save(Event event) {
        // Вставляем запись и возвращаем присвоенный PK
        final String sql = "INSERT INTO events (ts, user_id, event_type, operation, entity_id) VALUES (?,?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"event_id"});
            ps.setLong(1, event.getTimestamp());
            ps.setLong(2, event.getUserId());
            ps.setString(3, event.getEventType().name());
            ps.setString(4, event.getOperation().name());
            ps.setLong(5, event.getEntityId());
            return ps;
        }, kh);
        if (kh.getKey() != null) {
            event.setEventId(kh.getKey().longValue());
        }
        return event;
    }

    @Override
    public List<Event> findByUserIdOrderByTimestampAsc(long userId) {
        final String sql = "SELECT event_id, ts, user_id, event_type, operation, entity_id " +
                "FROM events WHERE user_id = ? ORDER BY ts ASC, event_id ASC";
        return jdbc.query(sql, mapper, userId);
    }
}
