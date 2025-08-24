package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final UserService userService;

    @Override
    public void addEvent(long userId, EventType type, Operation op, long entityId) {
        userService.find((int) userId);
        Event e = new Event();
        e.setTimestamp(System.currentTimeMillis());
        e.setUserId(userId);
        e.setEventType(type);
        e.setOperation(op);
        e.setEntityId(entityId);
        eventStorage.save(e);
    }

    @Override
    public List<EventDto> getFeed(long userId) {
        userService.find((int) userId);
        return EventMapper.toDtoList(eventStorage.findByUserIdOrderByTimestampAsc(userId));
    }
}
