package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate BIRTHDAY_OF_WORLD_CINEMA = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String FILM_RELEASE_DATE_ERROR_MESSAGE = "Некорректная дата релиза. Дата релиза должна быть не раньше 28 декабря 1895 года";

    private long counter = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Обновление фильма: {}", oldFilm);
            log.info("Информация для обновления: {}", newFilm);

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            validateReleaseDate(newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("Фильм обновлен: {}", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(BIRTHDAY_OF_WORLD_CINEMA)) {
            log.error(FILM_RELEASE_DATE_ERROR_MESSAGE);
            throw new ValidationException(FILM_RELEASE_DATE_ERROR_MESSAGE);
        }
    }
}
