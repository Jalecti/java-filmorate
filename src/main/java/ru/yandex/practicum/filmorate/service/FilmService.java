package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.FilmLikeRepository;
import ru.yandex.practicum.filmorate.dal.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserEventRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmLikeRepository filmLikeRepository;
    private final UserEventRepository userEventRepository;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;
    private final DirectorService directorService;

    private static final LocalDate BIRTHDAY_OF_WORLD_CINEMA = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String FILM_RELEASE_DATE_ERROR_MESSAGE =
            "Некорректная дата релиза. Дата релиза должна быть не раньше 28 декабря 1895 года";

    public Collection<FilmDto> findAll() {
        Map<Long, Integer> filmsLikesCount = getAllFilmsLikesCountMap();
        Map<Long, List<Genre>> filmsGenres = genreService.getAllFilmsGenresMap();
        Map<Long, List<Director>> filmsDirectors = directorService.getAllFilmsDirectorsMap();

        return filmRepository.findAll()
                .stream()
                .map(film -> {
                    Integer likesCount = filmsLikesCount.get(film.getId());
                    List<Genre> genres = filmsGenres.get(film.getId());
                    List<Director> directors = filmsDirectors.get(film.getId());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
                })
                .collect(Collectors.toList());
    }

    public FilmDto create(@Valid NewFilmRequest request) {
        validateReleaseDate(request.getReleaseDate());
        ratingService.checkRating(request.getMpa().getId());
        genreService.checkGenres(request.getGenres());
        directorService.checkDirectors(request.getDirectors());

        Film film = FilmMapper.mapToFilm(request);
        film = filmRepository.create(film);
        if (request.getGenres() != null) genreService.updateGenresForFilm(film.getId(), request.getGenres());
        if (request.getDirectors() != null) directorService.updateDirectorForFilm(film.getId(), request.getDirectors());

        return FilmMapper.mapToFilmDto(film, genreService.getFilmGenres(film.getId()), 0, directorService.getFilmDirectors(film.getId()));
    }

    public FilmDto update(UpdateFilmRequest request) {
        validateReleaseDate(request.getReleaseDate());
        ratingService.checkRating(request.getMpa().getId());
        genreService.checkGenres(request.getGenres());
        directorService.checkDirectors(request.getDirectors());

        Long filmId = request.getId();
        Film updatedFilm = filmRepository.getFilmById(filmId)
                .map(film -> {
                    if (request.hasGenres()) genreService.updateGenresForFilm(filmId, request.getGenres());
                    if (request.hasDirectors()) directorService.updateDirectorForFilm(filmId, request.getDirectors());
                    return FilmMapper.updateFilmFields(film, request);
                })
                .orElseThrow(() -> {
                    log.error("Фильм не найден с ID: {}", filmId);
                    return new NotFoundException("Фильм не найден с ID: " + filmId);
                });
        updatedFilm = filmRepository.update(updatedFilm);
        List<Genre> genres = genreService.getFilmGenres(filmId);
        List<Director> directors = directorService.getFilmDirectors(filmId);
        int likesCount = filmRepository.getCountLikes(filmId);
        return FilmMapper.mapToFilmDto(updatedFilm, genres, likesCount, directors);
    }

    public boolean delete(Long filmId) {
        return filmRepository.delete(filmId);
    }

    public FilmDto getFilmById(Long filmId) {
        return filmRepository.getFilmById(filmId)
                .map(film -> {
                    Integer likesCount = filmRepository.getCountLikes(film.getId());
                    List<Genre> genres = genreService.getFilmGenres(film.getId());
                    List<Director> directors = directorService.getFilmDirectors(film.getId());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
                })
                .orElseThrow(() -> {
                    log.error("Фильм не найден с ID: {}", filmId);
                    return new NotFoundException("Фильм не найден с ID: " + filmId);
                });
    }

    public void addLike(Long filmId, Long userId) {
        checkFilm(filmId);
        userService.checkUser(userId);

        filmRepository.addLike(filmId, userId);
        userEventRepository.addUserEvent(userId, filmId, EventType.LIKE.name(), EventOperation.ADD.name());
        log.info("Пользователю с id={} понравился фильм с id={}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilm(filmId);
        userService.checkUser(userId);

        filmRepository.deleteLike(filmId, userId);
        userEventRepository.addUserEvent(userId, filmId, EventType.LIKE.name(), EventOperation.REMOVE.name());
        log.info("Пользователь с id={} убрал лайк у фильма с id={}", userId, filmId);
    }

    public Map<Long, Integer> getAllFilmsLikesCountMap() {
        return filmLikeRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        FilmLike::getFilmId,
                        Collectors.summingInt(filmLike -> 1)
                ));
    }

    public Collection<FilmDto> getMostPopular(Long count, Long genreId, Integer year) {
        Map<Long, Integer> filmsLikesCount = getAllFilmsLikesCountMap();
        Map<Long, List<Genre>> filmsGenres = genreService.getAllFilmsGenresMap();
        Map<Long, List<Director>> filmDirectors = directorService.getAllFilmsDirectorsMap();

        return filmRepository.findMostPopular(count, genreId, year)
                .stream()
                .map(film -> {
                    Integer likesCount = filmsLikesCount.get(film.getId());
                    List<Genre> genres = filmsGenres.get(film.getId());
                    List<Director> directors = filmDirectors.get(film.getId());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
                })
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getByDirector(Long directorId, String sortBy) {
        Map<Long, Integer> filmsLikesCount = getAllFilmsLikesCountMap();
        Map<Long, List<Genre>> filmsGenres = genreService.getAllFilmsGenresMap();
        Map<Long, List<Director>> filmDirectors = directorService.getAllFilmsDirectorsMap();

        try {
            Comparator<FilmDto> comparator;

            switch (sortBy) {
                case "likes" :
                    comparator = Comparator.comparing(FilmDto::getLikesCount).reversed(); break;
                case "year" :
                    comparator = Comparator.comparing(FilmDto::getReleaseDate); break;
                default : throw new ValidationException("Неверное поле для сортировки");
                };

            return filmRepository.findByDirector(directorId)
                    .stream()
                    .map(film -> {
                        Integer likesCount = filmsLikesCount.get(film.getId());
                        List<Genre> genres = filmsGenres.get(film.getId());
                        List<Director> directors = filmDirectors.get(film.getId());
                        return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
                    })
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Ошибка при сортировке: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сортировке: " + e.getMessage());
        }
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        return filmRepository.getCommonFilms(userId,friendId).stream().map(film -> {
            Integer likesCount = filmRepository.getCountLikes(film.getId());
            List<Genre> genres = genreService.getFilmGenres(film.getId());
            List<Director> directors = directorService.getFilmDirectors(film.getId());

            return FilmMapper.mapToFilmDto(film, genres, likesCount,directors);
        }).collect(Collectors.toList());
    }

    private void checkFilm(Long filmId) {
        Optional<Film> film = filmRepository.getFilmById(filmId);
        if (film.isEmpty()) {
            log.error("Фильм не найден с ID: {}", filmId);
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
