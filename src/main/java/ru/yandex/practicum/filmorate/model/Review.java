package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public class Review {
    @NonFinal
    Long reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    @NonFinal
    Integer useful;
}
