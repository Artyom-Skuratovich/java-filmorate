package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.database.EventDbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(EventDbStorage.class)
@org.springframework.test.context.TestPropertySource(properties = "spring.sql.init.mode=never")
@Sql(scripts = {"/schema.sql", "/data.sql"})
class EventDbStorageTest {

    @Autowired
    private EventDbStorage storage;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void saveAndFindByUserId_shouldReturnOrderedAscending() {
        // rus: создаём пользователя, чтобы пройти FK users(id)
        jdbc.update("INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)",
                "feed@test.com", "u100", "Feed User", "1990-01-01");
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE login = ?", Long.class, "u100");

        // rus: готовим 2 события для одного пользователя с возрастающими метками времени
        Event e1 = Event.builder()
                .timestamp(1_000L)     // старее
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(10L)
                .build();

        Event e2 = Event.builder()
                .timestamp(2_000L)     // новее
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(20L)
                .build();

        storage.save(e2);  // rus: сохраняем в обратном порядке
        storage.save(e1);

        // rus: выборка должна быть по возрастанию ts (и event_id как tie-breaker)
        List<Event> feed = storage.findByUserIdOrderByTimestampAsc(userId);

        assertThat(feed).hasSize(2);
        assertThat(feed.get(0).getTimestamp()).isEqualTo(1_000L);
        assertThat(feed.get(1).getTimestamp()).isEqualTo(2_000L);

        // rus: проверим, что id присвоены
        assertThat(feed.get(0).getEventId()).isNotNull();
        assertThat(feed.get(1).getEventId()).isNotNull();
    }
}
