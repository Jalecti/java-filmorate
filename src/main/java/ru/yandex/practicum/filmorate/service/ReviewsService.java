package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewsService {
    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return reviewStorage.create(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.update(review);
    }

    public boolean deleteReview(long reviewId) {
        return reviewStorage.delete(reviewId);
    }

    public Review getReviewById(long reviewId) {
        return reviewStorage.find(reviewId);
    }

    public List<Review> getReviews(Integer filmId, Integer limit) {
        return reviewStorage.findAll(filmId, limit);
    }

    public Review addLike(long reviewId, long userId) {
        return reviewStorage.addLike(userId, reviewId, true);
    }

    public Review addDislike(long reviewId, long userId) {
        return reviewStorage.addLike(userId, reviewId, false);
    }

    public boolean removeLike(long reviewId, long userId) {
        return reviewStorage.removeLike(userId, reviewId, true);
    }

    public boolean removeDislike(long reviewId, long userId) {
        return reviewStorage.removeLike(userId, reviewId, false);
    }
}
