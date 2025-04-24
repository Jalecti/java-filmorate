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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return convertCollectionFilmToFilmDto(filmRepository.findAll());
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
                    genreService.updateGenresForFilm(filmId, request.getGenres());

                    directorService.updateDirectorForFilm(filmId, request.getDirectors());

                    return FilmMapper.updateFilmFields(film, request);
                })
                .orElseThrow(() -> {
                    log.error("Фильм не найден с ID: {}", filmId);
                    return new NotFoundException("Фильм не найден с ID: " + filmId);
                });
        updatedFilm = filmRepository.update(updatedFilm);
        return Stream.of(updatedFilm).map(filmDtoMapperLambda()).toList().getFirst();
    }

    public boolean delete(Long filmId) {
        return filmRepository.delete(filmId);
    }

    public FilmDto getFilmById(Long filmId) {
        return filmRepository.getFilmById(filmId)
                .map(filmDtoMapperLambda())
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
        return convertCollectionFilmToFilmDto(filmRepository.findMostPopular(count, genreId, year));
    }

    public Collection<FilmDto> getByDirector(Long directorId, String sortBy) {
        Optional<Director> director = directorService.findDirectorById(directorId);
        if (director.isEmpty()) {
            log.error("Директор не найден с ID: {}", directorId);
            throw new NotFoundException("Директор не найден с ID: " + directorId);
        }

        try {
            Comparator<FilmDto> comparator;

            switch (sortBy) {
                case "likes":
                    comparator = Comparator.comparing(FilmDto::getLikesCount).reversed();
                    break;
                case "year":
                    comparator = Comparator.comparing(FilmDto::getReleaseDate);
                    break;
                default:
                    throw new ValidationException("Неверное поле для сортировки");
            }

            return convertCollectionFilmToFilmDto(filmRepository.findByDirector(directorId))
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Ошибка при сортировке: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сортировке: " + e.getMessage());
        }
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        return convertCollectionFilmToFilmDto(filmRepository.getCommonFilms(userId, friendId));
    }

    public Collection<FilmDto> findAllByParams(String query, String byValues) {
        return convertCollectionFilmToFilmDto(filmRepository.findAllByParams(query, byValues));
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

    private Collection<FilmDto> convertCollectionFilmToFilmDto(Collection<Film> films) {
        Map<Long, Integer> filmsLikesCount = getAllFilmsLikesCountMap();
        Map<Long, List<Genre>> filmsGenres = genreService.getAllFilmsGenresMap();
        Map<Long, List<Director>> filmsDirectors = directorService.getAllFilmsDirectorsMap();
        return films.stream()
                .map(film -> {
                    Integer likesCount = filmsLikesCount.getOrDefault(film.getId(), 0);
                    List<Genre> genres = filmsGenres.getOrDefault(film.getId(), List.of());
                    List<Director> directors = filmsDirectors.getOrDefault(film.getId(), List.of());
                    return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
                }).toList();
    }

    private Function<Film, FilmDto> filmDtoMapperLambda() {
        return film -> {
            Integer likesCount = filmRepository.getCountLikes(film.getId());
            List<Genre> genres = genreService.getFilmGenres(film.getId());
            List<Director> directors = directorService.getFilmDirectors(film.getId());
            return FilmMapper.mapToFilmDto(film, genres, likesCount, directors);
        };
    }
}