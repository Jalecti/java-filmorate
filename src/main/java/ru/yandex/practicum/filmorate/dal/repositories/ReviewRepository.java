package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {
    private static final String INSERT_QUERY = "INSERT INTO reviews (review_id, " +
            "content, " +
            "is_positive, " +
            "user_id, " +
            "film_id) " +
            "VALUES (default, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT *," +
            " SUM(CASE WHEN COALESCE(rl.IS_POSITIVE, false) THEN 1 " +
            "ELSE (CASE WHEN rl.IS_POSITIVE IS NULL THEN 0 ELSE -1 END) END) AS useful" +
            " FROM reviews AS r " +
            "LEFT JOIN review_likes AS rl ON r.review_id = rl.review_id " +
            "WHERE r.review_id = ?" +
            "GROUP BY r.review_id";
    private static final String UPDATE_QUERY = "UPDATE reviews SET " +
            "content = ?, " +
            "is_positive = ? " +
            "WHERE REVIEW_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE REVIEW_ID = ?";
    private static final String FIND_ALL_REVIEW_OF_FILM_QUERY = "SELECT *," +
            " SUM(CASE WHEN COALESCE(rl.IS_POSITIVE, false) THEN 1 " +
            "ELSE (CASE WHEN rl.IS_POSITIVE IS NULL THEN 0 ELSE -1 END) END) AS useful" +
            " FROM reviews AS r " +
            "LEFT JOIN review_likes AS rl ON r.review_id = rl.review_id " +
            "WHERE r.FILM_ID = COALESCE(?, FILM_ID)" +
            "GROUP BY r.review_id " +
            "ORDER BY useful desc " +
            "LIMIT ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) " +
            "KEY (REVIEW_ID, USER_ID) " +
            "VALUES (?, ?, ?)";

    private static final  String DELETE_LIKE_QUERY = "DELETE FROM REVIEW_LIKES " +
            "WHERE REVIEW_ID = ? AND " +
            "USER_ID = ? AND " +
            "IS_POSITIVE = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        long id;
        try {
            id = insert(INSERT_QUERY,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUserId(),
                    review.getFilmId());
        } catch (RuntimeException e) {
            throw new NotFoundException("Ошибка в передаваемых данных " + e.getMessage());
        }
        return find(id);
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
                );
        return find(review.getReviewId());
    }

    @Override
    public boolean delete(long reviewId) {
        return delete(DELETE_QUERY, reviewId);
    }

    @Override
    public Review find(long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Review not found id=" + id));
    }

    @Override
    public List<Review> findAll(Long filmId, Integer limit) {
        if (limit == null) {
            limit = 10;
        }
            System.out.println(filmId + " - " + limit);
            return findMany(FIND_ALL_REVIEW_OF_FILM_QUERY, filmId, limit);
    }

    @Override
    public Review addLike(long userId, long reviewId, boolean isPositive) {
        jdbc.update(ADD_LIKE_QUERY, reviewId, userId, isPositive);
        return find(reviewId);
    }

    @Override
    public boolean removeLike(long userId, long reviewId, boolean isPositive) {
        return jdbc.update(DELETE_LIKE_QUERY, userId, reviewId, isPositive) > 0;
    }
}
