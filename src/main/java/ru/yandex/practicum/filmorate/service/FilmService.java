package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film delete(Long filmId) {
        return filmStorage.delete(filmId);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        userStorage.checkUser(userId);

        filmStorage.getFilmById(filmId).getUserIdLikes().add(userId);
        log.info("Пользователю с id={} понравился фильм с id={}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        userStorage.checkUser(userId);

        filmStorage.getFilmById(filmId).getUserIdLikes().remove(userId);
        log.info("Пользователь с id={} убрал лайк у фильма с id={}", userId, filmId);
    }

    public Collection<Film> getMostPopular(int count) {
        List<Film> mostPopularFilms = new ArrayList<>(filmStorage.findAll());
        mostPopularFilms.sort((f1, f2) -> f2.getUserIdLikes().size() - f1.getUserIdLikes().size());

        return mostPopularFilms.subList(0, Math.min(mostPopularFilms.size(), count));
    }
}
