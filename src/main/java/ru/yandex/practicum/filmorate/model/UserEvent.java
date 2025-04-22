package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
