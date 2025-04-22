package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"eventId"})
public class UserEvent {
    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull
    private EventType eventType;

    @NotNull
    private EventOperation operation;

    @NotNull
    private Long entityId;

    @NotNull
    private Long timestamp;
}
