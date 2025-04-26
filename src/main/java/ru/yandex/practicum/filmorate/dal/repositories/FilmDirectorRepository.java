package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.List;

@Repository
public class FilmDirectorRepository extends BaseRepository implements FilmDirectorStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM film_directors";

    private static final String DELETE_QUERY =
            "DELETE FROM film_genres " +
                    "WHERE film_id = ?";

    private static final String INSERT_QUERY =
            "MERGE INTO film_genres (film_id, genre_id) " +
                    "KEY(film_id, genre_id) " +
                    "VALUES (?, ?)";

    public FilmDirectorRepository(JdbcTemplate jdbc, RowMapper<FilmDirector> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmDirector> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}