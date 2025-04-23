package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, r.rating_name FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "ORDER BY f.film_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, rating_name FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "WHERE film_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO films(film_name, description, duration, release_date, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE films SET " +
                    "film_name = ?," +
                    "description = ?, " +
                    "duration = ?, " +
                    "release_date = ?, " +
                    "rating_id = ? " +
                    "WHERE film_id = ? ";

    private static final String DELETE_QUERY =
            "DELETE FROM films " +
                    "WHERE film_id = ?";

    private static final String LIKES_COUNT_QUERY =
            "SELECT COUNT(*) FROM users_film_likes " +
                    "WHERE film_id = ?";

    private static final String ADD_LIKE_QUERY =
            "MERGE INTO users_film_likes(film_id, user_id) " +
                    "KEY(film_id, user_id)" +
                    "VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM users_film_likes " +
                    "WHERE film_id = ? AND user_id = ?";

    private static final String DELETE_ALL_QUERY =
            "DELETE FROM films; " +
                    "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1";

    private static final String DELETE_ALL_FILMS_LIKES =
            "DELETE FROM users_film_likes;";

    private static final String FIND_MOST_POPULAR_QUERY =
            "SELECT f.*, r.rating_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN users_film_likes AS ufl ON f.film_id = ufl.film_id " +
                    "where coalesce(?, EXTRACT(YEAR from release_date)) = EXTRACT(YEAR from release_date) " +
                    "      and exists (select 1 from film_genres as fg where fg.film_id = f.film_id and coalesce(?, fg.genre_id) = fg.genre_id) " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(ufl.user_id) DESC " +
                    "LIMIT ?";
    private static final String GET_COMMON_FILMS_QUERY = "SELECT f.*, r.RATING_NAME FROM films AS f " +
            "LEFT JOIN ratings AS r ON f.rating_id = r.rating_id " +
            "INNER  JOIN USERS_FILM_LIKES AS ufl1 ON f.FILM_ID = ufl1.film_id " +
            "INNER  JOIN USERS_FILM_LIKES AS ufl2 ON f.FILM_ID = ufl2.film_id " +
            "WHERE ufl1.user_id = ? AND ufl2.user_id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY (SELECT COUNT(*) FROM users_film_likes WHERE film_id = f.film_id) DESC ";

    private static final String FIND_FILMS_LIKED_BY_USERS_QUERY =
            "SELECT f.*, r.rating_name FROM films AS f INNER JOIN users_film_likes AS ufl ON f.film_id = ufl.film_id INNER JOIN ratings AS r ON f.rating_id = r.rating_id WHERE ufl.user_id IN (%s) %s GROUP BY f.film_id";

    private static final String FIND_FILMS_LIKED_BY_USERS_QUERY =
            "SELECT f.*, r.rating_name FROM films AS f INNER JOIN users_film_likes AS ufl ON f.film_id = ufl.film_id INNER JOIN ratings AS r ON f.rating_id = r.rating_id WHERE ufl.user_id IN (%s) %s GROUP BY f.film_id";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> getFilmById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film create(Film film) {
        long ratingId;
        if (film.getMpa() == null) {
            ratingId = 1;
        } else {
            ratingId = film.getMpa().getId();
        }

        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                ratingId
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    public boolean delete(Long filmId) {
        return delete(DELETE_QUERY, filmId);
    }

    public Integer getCountLikes(Long filmId) {
        return jdbc.queryForObject(LIKES_COUNT_QUERY, Integer.class, filmId);
    }

    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY);
    }

    public void deleteAllFilmLikes() {
        jdbc.update(DELETE_ALL_FILMS_LIKES);
    }

    public Collection<Film> findMostPopular(Long count, Long genreId, Integer year) {
        return findMany(FIND_MOST_POPULAR_QUERY, year, genreId, count);
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return findMany(GET_COMMON_FILMS_QUERY, userId, friendId);
    }

    public Collection<Long> getLikedFilmIdsByUserId(Long userId) {
        String query = "SELECT film_id FROM users_film_likes WHERE user_id = ?";
        return jdbc.queryForList(query, Long.class, userId);
    }

    public Collection<Film> findFilmsLikedByUsers(Collection<Long> similarUserIds, Collection<Long> likedFilmIds) {
        if (similarUserIds == null || similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholdersForUsers = String.join(",", Collections.nCopies(similarUserIds.size(), "?"));

        String additionalConditionForLikedFilms = (likedFilmIds != null && !likedFilmIds.isEmpty()) ?
                String.format("AND f.film_id NOT IN (%s)",
                        String.join(",", Collections.nCopies(likedFilmIds.size(), "?"))) : "";

        String sql = String.format(FIND_FILMS_LIKED_BY_USERS_QUERY,
                placeholdersForUsers,
                additionalConditionForLikedFilms);

        Object[] params = new Object[similarUserIds.size() + (likedFilmIds != null ? likedFilmIds.size() : 0)];

        int index = 0;

        for (Long userId : similarUserIds) {
            params[index++] = userId;
        }

        if (likedFilmIds != null) {
            for (Long filmId : likedFilmIds) {
                params[index++] = filmId;
            }
        }

        return jdbc.query(sql, params, mapper);
    }

    public Collection<Long> getLikedFilmIdsByUserId(Long userId) {
        String query = "SELECT film_id FROM users_film_likes WHERE user_id = ?";
        return jdbc.queryForList(query, Long.class, userId);
    }

    public Collection<Film> findFilmsLikedByUsers(Collection<Long> similarUserIds, Collection<Long> likedFilmIds) {
        if (similarUserIds == null || similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholdersForUsers = String.join(",", Collections.nCopies(similarUserIds.size(), "?"));

        String additionalConditionForLikedFilms = (likedFilmIds != null && !likedFilmIds.isEmpty()) ?
                String.format("AND f.film_id NOT IN (%s)",
                        String.join(",", Collections.nCopies(likedFilmIds.size(), "?"))) : "";

        String sql = String.format(FIND_FILMS_LIKED_BY_USERS_QUERY,
                placeholdersForUsers,
                additionalConditionForLikedFilms);

        Object[] params = new Object[similarUserIds.size() + (likedFilmIds != null ? likedFilmIds.size() : 0)];

        int index = 0;

        for (Long userId : similarUserIds) {
            params[index++] = userId;
        }

        if (likedFilmIds != null) {
            for (Long filmId : likedFilmIds) {
                params[index++] = filmId;
            }
        }

        return jdbc.query(sql, params, mapper);
    }
}
