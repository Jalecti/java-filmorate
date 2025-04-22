package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewsService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewsService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public boolean deleteReview(@PathVariable long reviewId) {
        return reviewsService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable long reviewId) {
        return reviewsService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                   @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewsService.getReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addLike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewsService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review dislike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewsService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public boolean removeLike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewsService.removeLike(userId, reviewId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public boolean removeDislike(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewsService.removeDislike(userId, reviewId);
    }
}
