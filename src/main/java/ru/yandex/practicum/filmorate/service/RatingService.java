package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.RatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class RatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Collection<Rating> findAll() {
        return ratingRepository.findAll();
    }

    public Rating findRatingById(Long ratingId) {
        Optional<Rating> rating = ratingRepository.getRatingById(ratingId);
        if (rating.isEmpty()) {
            log.error("Рейтинг не найден с ID: {}", ratingId);
            throw new NotFoundException("Рейтинг не найден с ID: " + ratingId);
        }
        return rating.get();
    }

    public void checkRating(Long ratingId) {
        Optional<Rating> rating = ratingRepository.getRatingById(ratingId);
        if (rating.isEmpty()) {
            log.error("Рейтинг не найден с ID: {}", ratingId);
            throw new NotFoundException("Рейтинг не найден с ID: " + ratingId);
        }
    }
}