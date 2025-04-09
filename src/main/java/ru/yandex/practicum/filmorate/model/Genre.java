package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
