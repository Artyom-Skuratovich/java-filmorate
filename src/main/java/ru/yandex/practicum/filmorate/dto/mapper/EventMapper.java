package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

// Отдельный класс для преобразования моделей
public final class EventMapper {

    private EventMapper() {
    }

    // модель -> DTO
    public static EventDto toDto(Event e) {
        return EventDto.builder()
                .eventId(e.getEventId())
                .timestamp(e.getTimestamp())
                .userId(e.getUserId())
                .eventType(e.getEventType())
                .operation(e.getOperation())
                .entityId(e.getEntityId())
                .build();
    }
}
