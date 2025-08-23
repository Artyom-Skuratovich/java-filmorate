package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // регистрируем маппер как Spring‑бин
public class EventRowMapper implements RowMapper<Event> {

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
}
