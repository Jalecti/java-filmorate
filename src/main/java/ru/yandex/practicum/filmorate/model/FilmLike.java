package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"filmId", "userId"})
public class FilmLike {
    @NotNull
    private Long filmId;

    @NotNull
    private Long userId;
}
