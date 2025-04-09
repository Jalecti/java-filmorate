package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM genres " +
                    "ORDER BY genre_id";

    private static final String FIND_BY_ID =
            "SELECT * FROM genres " +
                    "WHERE genre_id = ?";

    private static final String FIND_ALL_BY_FILM_QUERY =
            "SELECT g.genre_id, g.genre_name FROM genres AS g " +
                    "INNER JOIN film_genres AS fg ON g.genre_id = fg.genre_id " +
                    "WHERE fg.film_id = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM film_genres " +
                    "WHERE film_id = ?";

    private static final String INSERT_QUERY =
            "MERGE INTO film_genres (film_id, genre_id) " +
                    "KEY(film_id, genre_id) " +
                    "VALUES (?, ?)";

    private static final String GET_ID_BY_NAME_QUERY =
            "SELECT genre_id FROM genres " +
                    "WHERE genre_name = ?";

    private static final String DELETE_ALL_FILM_GENRES_QUERY =
            "DELETE FROM film_genres";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getFilmGenres(Long filmId) {
        return findMany(FIND_ALL_BY_FILM_QUERY, filmId);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findGenreById(Long genreId) {
        return findOne(FIND_BY_ID, genreId);
    }

    public List<Genre> updateGenresForFilm(Long filmId, List<Genre> genres) {
        delete(DELETE_QUERY, filmId);
        genres.forEach(genre -> {
            if (findGenreById(genre.getId()).isPresent()) {
                jdbc.update(INSERT_QUERY, filmId, genre.getId());
            }
        });
        return getFilmGenres(filmId);
    }

    public void deleteAllFilmGenres() {
        jdbc.update(DELETE_ALL_FILM_GENRES_QUERY);
    }
}
