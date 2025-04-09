package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmRequest {
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @Positive
    private Integer duration;

    @NotNull
    private LocalDate releaseDate;

    private Rating mpa;

    private List<Genre> genres;
}
