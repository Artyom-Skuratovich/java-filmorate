package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface EventService {
    // Добавить событие
    void addEvent(long userId, EventType type, Operation op, long entityId);

    // Получить ленту пользователя
    List<Event> getFeed(long userId);
}
