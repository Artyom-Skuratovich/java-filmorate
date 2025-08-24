package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public final class EventMapperCompat {

    private EventMapperCompat() {
    }

    public static List<EventDto> toDtoList(List<Event> events) {
        return events.stream().map(EventMapperCompat::toDto).toList();
    }

    private static EventDto toDto(Event e) {

        EventDto dto = new EventDto();
        dto.setEventId(e.getEventId());
        dto.setTimestamp(e.getTimestamp());
        dto.setUserId(e.getUserId());
        dto.setEventType(e.getEventType());
        dto.setOperation(e.getOperation());
        dto.setEntityId(e.getEntityId());
        return dto;
    }
}