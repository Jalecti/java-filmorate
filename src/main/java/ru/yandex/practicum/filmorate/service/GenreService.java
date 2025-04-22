package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final FilmGenreRepository filmGenreRepository;

    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre findGenreById(Long genreId) {
        Optional<Genre> genre = genreRepository.findGenreById(genreId);
        if (genre.isEmpty()) {
            log.error("Жанр не найден с ID: {}", genreId);
            throw new NotFoundException("Жанр не найден с ID: " + genreId);
        }
        return genre.get();
    }

    public List<Genre> getFilmGenres(Long filmId) {
        return genreRepository.getFilmGenres(filmId);
    }

    public Map<Long, List<Genre>> getAllFilmsGenresMap() {
        Map<Long, Genre> genreMap = findAll().stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        return filmGenreRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        FilmGenre::getFilmId,
                        Collectors.mapping(filmGenre -> genreMap.get(filmGenre.getGenreId()), Collectors.toList())
                ));
    }

    public List<Genre> updateGenresForFilm(Long filmId, List<Genre> genres) {
        return genreRepository.updateGenresForFilm(filmId, genres);
    }

    public void checkGenres(List<Genre> genres) {
        if (genres != null) {
            genres.forEach(g -> {
                Long genreId = g.getId();
                Optional<Genre> genre = genreRepository.findGenreById(genreId);
                if (genre.isEmpty()) {
                    log.error("Жанр не найден с ID: {}", genreId);
                    throw new NotFoundException("Жанр не найден с ID: " + genreId);
                }
            });
        }
    }
}
