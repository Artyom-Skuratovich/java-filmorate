package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.mapper.EventMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final EventStorage eventStorage;

    // явно указываем реализацию хранилища пользователей (DB)
    private final @Qualifier("userDbStorage") UserStorage userStorage;

    @Override
    public void addEvent(long userId, EventType type, Operation op, long entityId) {
        // валидация пользователя
        userStorage.find((int) userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d не найден", userId)));

        // создаём и сохраняем событие
        Event event = new Event();
        event.setTimestamp(System.currentTimeMillis());
        event.setUserId(userId);
        event.setEventType(type);
        event.setOperation(op);
        event.setEntityId(entityId);

        eventStorage.save(event);
    }

    @Override
    public List<EventDto> getFeed(long userId) {
        // валидация пользователя
        userStorage.find((int) userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d не найден", userId)));

        // получаем события и мапим в DTO
        return eventStorage.findByUserIdOrderByTimestampAsc(userId)
                .stream()
                .map(EventMapper::toDto)
                .toList();
    }
}