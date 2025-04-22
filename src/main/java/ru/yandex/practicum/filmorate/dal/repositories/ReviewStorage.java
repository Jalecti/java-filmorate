package ru.yandex.practicum.filmorate.dal.repositories;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    boolean delete(long reviewId);

    Review find(long id);

    List<Review> findAll(Integer filmId, Integer limit);

    Review addLike(long userId, long reviewId, boolean isPositive);

    boolean removeLike(long userId, long reviewId, boolean isPositive);
}
