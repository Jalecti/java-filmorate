package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;

    private static final LocalDate BIRTHDAY_OF_WORLD_CINEMA = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String FILM_RELEASE_DATE_ERROR_MESSAGE =
            "Некорректная дата релиза. Дата релиза должна быть не раньше 28 декабря 1895 года";

    @Autowired
    public FilmService(FilmRepository filmRepository,
                       UserService userService,
                       RatingService ratingService,
                       GenreService genreService) {
        this.filmRepository = filmRepository;
        this.userService = userService;
        this.ratingService = ratingService;
        this.genreService = genreService;

    }

    public Collection<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(film -> {
                    Integer likesCount = filmRepository.getCountLikes(film.getId());
                    List<Genre> genres = genreService.getFilmGenres(film.getId());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount);
                })
                .collect(Collectors.toList());
    }

    public FilmDto create(@Valid NewFilmRequest request) {
        validateReleaseDate(request.getReleaseDate());
        ratingService.checkRating(request.getMpa().getId());
        genreService.checkGenres(request.getGenres());

        Film film = FilmMapper.mapToFilm(request);
        film = filmRepository.create(film);
        if (request.getGenres() != null) genreService.updateGenresForFilm(film.getId(), request.getGenres());

        return FilmMapper.mapToFilmDto(film, genreService.getFilmGenres(film.getId()), 0);
    }

    public FilmDto update(UpdateFilmRequest request) {
        validateReleaseDate(request.getReleaseDate());
        ratingService.checkRating(request.getMpa().getId());
        genreService.checkGenres(request.getGenres());

        Long filmId = request.getId();
        Film updatedFilm = filmRepository.getFilmById(filmId)
                .map(film -> {
                    if (request.hasGenres()) genreService.updateGenresForFilm(filmId, request.getGenres());
                    return FilmMapper.updateFilmFields(film, request);
                })
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        updatedFilm = filmRepository.update(updatedFilm);
        List<Genre> genres = genreService.getFilmGenres(filmId);
        int likesCount = filmRepository.getCountLikes(filmId);
        return FilmMapper.mapToFilmDto(updatedFilm, genres, likesCount);
    }

    public boolean delete(Long filmId) {
        return filmRepository.delete(filmId);
    }

    public FilmDto getFilmById(Long filmId) {
        return filmRepository.getFilmById(filmId)
                .map(film -> {
                    Integer likesCount = filmRepository.getCountLikes(film.getId());
                    List<Genre> genres = genreService.getFilmGenres(film.getId());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount);
                })
                .orElseThrow(() -> new NotFoundException("Фильм не найден с ID: " + filmId));
    }

    public void addLike(Long filmId, Long userId) {
        checkFilm(filmId);
        userService.checkUser(userId);

        filmRepository.addLike(filmId, userId);
        log.info("Пользователю с id={} понравился фильм с id={}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilm(filmId);
        userService.checkUser(userId);

        filmRepository.deleteLike(filmId, userId);
        log.info("Пользователь с id={} убрал лайк у фильма с id={}", userId, filmId);
    }

    public Collection<FilmDto> getMostPopular(int count) {
        List<FilmDto> mostPopularFilms = new ArrayList<>(findAll());
        mostPopularFilms.sort((f1, f2) -> f2.getLikesCount() - f1.getLikesCount());

        return mostPopularFilms.subList(0, Math.min(mostPopularFilms.size(), count));
    }

    private void checkFilm(Long filmId) {
        Optional<Film> film = filmRepository.getFilmById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм не найден с ID: " + filmId);
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(BIRTHDAY_OF_WORLD_CINEMA)) {
            log.error(FILM_RELEASE_DATE_ERROR_MESSAGE);
            throw new ValidationException(FILM_RELEASE_DATE_ERROR_MESSAGE);
        }
    }

}
