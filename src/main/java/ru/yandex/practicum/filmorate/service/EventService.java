package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface EventService {

    // добавить событие в ленту
    void addEvent(long userId, EventType type, Operation op, long entityId);

    // получить ленту пользователя по userId
    List<Event> getFeed(long userId);
}