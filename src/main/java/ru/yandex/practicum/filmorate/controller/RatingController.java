package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Rating> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{ratingId}")
    public Rating findGenreById(@PathVariable Long ratingId) {
        return ratingService.findRatingById(ratingId);
    }
}
