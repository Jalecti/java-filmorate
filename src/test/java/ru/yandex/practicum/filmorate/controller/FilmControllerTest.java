package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @Test
    public void shouldThrowValidationExceptionWhenCreateFilmAndReleaseDateIsBeforeThenBirthdayOfWorldCinema() {
        assertThrows(ValidationException.class, () -> {
            Film film = new Film();
            film.setReleaseDate(LocalDate.of(1895, 12, 27));
            new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage())).create(film);
        });

        assertDoesNotThrow(() -> {
            Film film = new Film();
            film.setReleaseDate(LocalDate.of(1895, 12, 28));
            Film created = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage())).create(film);
        });
    }

    @Test
    public void shouldThrowValidationExceptionWhenUpdateFilmAndReleaseDateIsBeforeThenBirthdayOfWorldCinema() {
        assertThrows(ValidationException.class, () -> {
            FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
            Film film = new Film();
            film.setReleaseDate(LocalDate.of(2000, 1, 1));
            filmController.create(film);
            Film filmToUpdate = new Film();
            filmToUpdate.setId(1L);
            filmToUpdate.setReleaseDate(LocalDate.of(1895, 12, 27));
            Film updated = filmController.update(filmToUpdate);
        });

        assertDoesNotThrow(() -> {
            FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
            Film film = new Film();
            film.setReleaseDate(LocalDate.of(2000, 1, 1));
            filmController.create(film);
            Film filmToUpdate = new Film();
            filmToUpdate.setId(1L);
            filmToUpdate.setReleaseDate(LocalDate.of(1895, 12, 28));
            Film updated = filmController.update(filmToUpdate);
        });
    }
}