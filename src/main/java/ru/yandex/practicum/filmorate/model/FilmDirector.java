package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"filmId", "genreId"})
public class FilmDirector {
    @NotNull
    private Long filmId;

    @NotNull
    private Long directorId;
}


