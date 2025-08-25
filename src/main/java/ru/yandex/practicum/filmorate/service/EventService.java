package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.StorageUtils;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<Event> findForUser(int userId) {
        StorageUtils.findModel(userStorage, userId, String.format(
                "Пользователь с id=%d не найден",
                userId
        ));
        return eventStorage.findForUser(userId);
    }

    public Event create(int userId, int entityId, EventType eventType, Operation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(entityId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(Instant.now().toEpochMilli());
        return eventStorage.create(event);
    }
}