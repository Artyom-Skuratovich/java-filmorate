package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

// DTO для соответствия контракту JSON
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    @JsonProperty("eventId")
    private long eventId;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("operation")
    private Operation operation;

    @JsonProperty("entityId")
    private long entityId;

    // Маппер из модели Event в DTO
    public static EventDto from(Event e) {
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
