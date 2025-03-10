package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate BIRTHDAY_OF_WORLD_CINEMA = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String FILM_RELEASE_DATE_ERROR_MESSAGE = "Некорректная дата релиза. Дата релиза должна быть не раньше 28 декабря 1895 года";

    private long counter = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Поиск всех фильмов");
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        validateReleaseDate(newFilm.getReleaseDate());

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Обновление фильма: {}", oldFilm);
            log.info("Информация для обновления: {}", newFilm);

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("Фильм обновлен: {}", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film delete(Long filmId) {
        Film deleted = checkFilm(filmId);
        films.remove(filmId);
        log.info("Фильм с id={} удален", filmId);
        return deleted;
    }

    @Override
    public Film checkFilm(Long filmId) {
        if (filmId == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(filmId)) {
            log.error("Фильм с id={} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }

        return films.get(filmId);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return checkFilm(filmId);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(BIRTHDAY_OF_WORLD_CINEMA)) {
            log.error(FILM_RELEASE_DATE_ERROR_MESSAGE);
            throw new ValidationException(FILM_RELEASE_DATE_ERROR_MESSAGE);
        }
    }
}
