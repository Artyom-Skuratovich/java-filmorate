package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    // идентификатор события (PK)
    private long eventId;

    // метка времени в миллисекундах (System.currentTimeMillis())
    private long timestamp;

    // пользователь, для которого зафиксировано событие
    private long userId;

    // тип события: LIKE | REVIEW | FRIEND
    private EventType eventType;

    // операция: ADD | REMOVE | UPDATE
    private Operation operation;

    // идентификатор сущности (filmId, reviewId, friendId)
    private long entityId;
}