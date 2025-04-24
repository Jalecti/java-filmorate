package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.repositories.UserEventRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewsService {
    private final ReviewStorage reviewStorage;
    private final UserEventRepository userEventRepository;

    public Review addReview(Review review) {
        validateReview(review);
        Review addedReview = reviewStorage.create(review);
        userEventRepository.addUserEvent(addedReview.getUserId(), addedReview.getReviewId(),
                EventType.REVIEW.name(), EventOperation.ADD.name());
        return addedReview;
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.update(review);
        userEventRepository.addUserEvent(updatedReview.getUserId(), updatedReview.getReviewId(),
                EventType.REVIEW.name(), EventOperation.UPDATE.name());
        return updatedReview;
    }

    public boolean deleteReview(long reviewId) {
        Long userId = getReviewById(reviewId).getUserId();
        boolean hasBeenDeleted = reviewStorage.delete(reviewId);
        if (hasBeenDeleted) {
            userEventRepository.addUserEvent(userId, reviewId,
                    EventType.REVIEW.name(), EventOperation.REMOVE.name());
        }
        return hasBeenDeleted;
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

    private void validateReview(Review review) {
        String errorMessage = "Ошибка в передаваемых данных: ";
        if (review.getContent() == null) {
            throw new ValidationException(errorMessage + "content=" + review.getContent());
        }
        if (review.getUserId() == null) {
            throw new ValidationException(errorMessage + "userId=" + review.getUserId());
        }
        if (review.getUserId() <= 0) {
            throw new NotFoundException(errorMessage + "userId=" + review.getUserId());
        }
        if (review.getFilmId() == null) {
            throw new ValidationException(errorMessage + "filmId=" + review.getFilmId());
        }
        if (review.getFilmId() <= 0) {
            throw new NotFoundException(errorMessage + "filmId=" + review.getFilmId());
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException(errorMessage + "isPositive=" + review.getIsPositive());
        }
    }
}
