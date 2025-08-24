package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final EventStorage eventStorage;

    @Override
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
    public List<EventDto> getFeed(long userId) {
        return EventMapper.toDtoList(
                eventStorage.findByUserIdOrderByTimestampAsc(userId)
        );
    }
}