package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.repositories.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    public Collection<Director> findAll() {
        return new ArrayList<>(directorStorage.findAll());
    }

    public Director findDirectorBy(Long directorId) {
        return directorStorage.getDirectorById(directorId)
                .orElseThrow(() -> {
                    log.error("Режиссер не найден с ID: {}", directorId);
                    return new NotFoundException("Режиссер не найден с ID: " + directorId);
                }
                );
    }

    public Director create(@Valid Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void delete(Long directorId) {
        directorStorage.delete(directorId);
    }

    public List<Director> getFilmDirectors(Long filmId) {
        return directorStorage.getFilmDirectors(filmId);
    }

    public Map<Long, List<Director>> getAllFilmsDirectorsMap() {
        Map<Long, Director> directorMap = findAll().stream()
                .collect(Collectors.toMap(Director::getId, director -> director));

        return filmDirectorStorage.findAll().stream()
                .collect(Collectors.groupingBy(
                        FilmDirector::getDirectorId,
                        Collectors.mapping(filmGenre -> directorMap.get(filmGenre.getDirectorId()), Collectors.toList())
                ));
    }

    public List<Director> updateDirectorForFilm(Long filmId, List<Director> directors) {
        return directorStorage.updateDirectorsForFilm(filmId, directors);
    }

    public void checkDirectors(List<Director> directors) {
        if (directors != null) {
            directors.forEach(g -> {
                Long directorId = g.getId();
                Optional<Director> director = directorStorage.findDirectorById(directorId);
                if (director.isEmpty()) {
                    log.error("Режиссер не найден с ID: {}", directorId);
                    throw new NotFoundException("Режиссер не найден с ID: " + directorId);
                }
            });
        }
    }
}
