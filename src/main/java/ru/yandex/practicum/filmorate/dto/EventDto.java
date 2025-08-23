package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
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

    // Маппер перенесён в отдельный класс EventMapper
}
