package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public class Review {
    @NonFinal
    Integer reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    @NonFinal
    Integer useful;
}
