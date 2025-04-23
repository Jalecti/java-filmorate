package ru.yandex.practicum.filmorate.dal.repositories;

import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;

public interface FilmDirectorStorage {
    List<FilmDirector> findAll();
}
