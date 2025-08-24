package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private long eventId;
    private long timestamp;
    private long userId;
    private EventType eventType;
    private Operation operation;
    private long entityId;
}