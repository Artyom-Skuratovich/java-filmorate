package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface EventService {
    void addEvent(long userId, EventType type, Operation op, long entityId);

    List<EventDto> getFeed(long userId);
}
