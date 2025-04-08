package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }

    public Genre findGenreById(Long genreId) {
        return genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр не найден с ID: " + genreId));
    }

    public List<Genre> getFilmGenres(Long filmId) {
        return genreRepository.getFilmGenres(filmId);
    }

    public List<Genre> updateGenresForFilm(Long filmId, List<Genre> genres) {
        return genreRepository.updateGenresForFilm(filmId, genres);
    }

    public void checkGenres(List<Genre> genres) {
        if (genres != null) {
            genres.forEach(g -> {
                Optional<Genre> genre = genreRepository.findGenreById(g.getId());
                if (genre.isEmpty()) {
                    throw new NotFoundException("Жанр не найден с ID: " + g.getId());
                }
            });
        }

    }
}
