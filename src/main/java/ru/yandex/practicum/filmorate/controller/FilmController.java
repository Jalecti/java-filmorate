package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopular(@RequestParam(required = false, defaultValue = "10") Long count, @RequestParam(required = false) Long genreId, @RequestParam(required = false) Integer year) {
        return filmService.getMostPopular(count, genreId, year);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest filmRequest) {
        return filmService.create(filmRequest);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable("filmId") Long filmId,
                        @PathVariable("userId") Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@PathVariable Long filmId) {
        return filmService.delete(filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable("filmId") Long filmId,
                           @PathVariable("userId") Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getByDirector(@PathVariable("directorId") Long directorId, @RequestParam("sortBy") String sortBy) {
        Collection<FilmDto> filmsByDirector = filmService.getByDirector(directorId, sortBy);

        return filmsByDirector;
    }

}
