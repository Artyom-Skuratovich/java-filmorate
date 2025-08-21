package ru.yandex.practicum.filmorate.model;

import lombok.*;

// Модель события в ленте
@Data                   // генерирует get/set/toString/hashCode/equals
@NoArgsConstructor      // пустой конструктор
@AllArgsConstructor     // конструктор со всеми полями
@Builder                // билдер
public class Event {
    private long eventId;        // PK
    private long timestamp;      // epoch millis (колонка ts)
    private long userId;         // кто совершил действие
    private EventType eventType; // тип события: LIKE/REVIEW/FRIEND
    private Operation operation; // операция: ADD/REMOVE/UPDATE
    private long entityId;       // id связанной сущности
}
