package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class RecommendationService {
    private final UserService userService;
    private final FilmService filmService;

    public Collection<FilmDto> getRecommendationsForUser(Long userId) {
        userService.checkUser(userId);

        Collection<Long> likedFilmIds = filmService.getLikedFilmIdsByUserId(userId);

        if (likedFilmIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> similarUserIds = userService.findUsersByLikedFilmIds(likedFilmIds);

        Collection<Film> recommendedFilms = filmService.findFilmsLikedByUsers(similarUserIds, likedFilmIds);

        return filmService.convertCollectionFilmToFilmDto(recommendedFilms);
    }
}
