package ru.yandex.practicum.filmorate.dal.repositories;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> getDirectorById(Long id);

    Director create(Director director);

    Director update(Director director);

    void delete(Long id);

    List<Director> getFilmDirectors(Long filmId);

    Optional<Director> findDirectorById(Long directorId);

    List<Director> updateDirectorsForFilm(Long filmId, List<Director> directors);

}
