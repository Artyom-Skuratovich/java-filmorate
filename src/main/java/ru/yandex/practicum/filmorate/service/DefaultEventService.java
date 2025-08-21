package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

// Сервис ленты событий (запись и выдача)
@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final EventStorage eventStorage; // <-- niente UserService

    @Override
    @Transactional
    public void addEvent(long userId, EventType type, Operation op, long entityId) {
        Event e = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(type)
                .operation(op)
                .entityId(entityId)
                .build();
        eventStorage.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getFeed(long userId) {
        // Никакой валидации здесь — только доступ к БД
        return eventStorage.findByUserIdOrderByTimestampAsc(userId);
    }
}
