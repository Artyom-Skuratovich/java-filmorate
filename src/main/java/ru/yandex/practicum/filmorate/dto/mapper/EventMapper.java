package ru.yandex.practicum.filmorate.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public EventDto toDto(Event e) {
        return new EventDto(
                e.getEventId(),
                e.getTimestamp(),
                e.getUserId(),
                e.getEventType(),
                e.getOperation(),
                e.getEntityId()
        );
    }

    public List<EventDto> toDtoList(List<Event> events) {
        return events.stream().map(EventMapper::toDto).collect(Collectors.toList());
    }
}