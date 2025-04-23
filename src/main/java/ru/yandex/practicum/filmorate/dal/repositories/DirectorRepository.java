package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository implements DirectorStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM directors " +
                    "ORDER BY director_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM directors " +
                    "WHERE director_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO directors(name) " +
                    "VALUES (?)";

    private static final String UPDATE_QUERY =
            "UPDATE director SET " +
                    "name = ? " +
                    "WHERE director_id = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM directors " +
                    "WHERE director_id = ?";

    private static final String FIND_ALL_BY_FILM_QUERY =
            "SELECT d.director_id, d.name FROM directors AS d " +
                    "INNER JOIN film_directors AS fd ON d.director_id = fd.director_id " +
                    "WHERE fd.film_id = ?";

    private static final String DELETE_FILM_DIRECTOR_QUERY =
            "DELETE FROM film_directors " +
                    "WHERE film_id = ?";

    private static final String INSERT_FILM_DIRECTOR_QUERY =
            "MERGE INTO film_directors (film_id, director_id) " +
                    "KEY(film_id, director_id) " +
                    "VALUES (?, ?)";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> getDirectorById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director create(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );

        director.setId(id);

        return director;
    }

    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName()
        );
        return director;
    }

    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Director> getFilmDirectors(Long filmId) {
        return findMany(FIND_ALL_BY_FILM_QUERY, filmId);
    }

    public Optional<Director> findDirectorById(Long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    public List<Director> updateDirectorsForFilm(Long filmId, List<Director> directors) {
        delete(DELETE_FILM_DIRECTOR_QUERY, filmId);
        directors.forEach(director -> {
            if (findDirectorById(director.getId()).isPresent()) {
                jdbc.update(INSERT_FILM_DIRECTOR_QUERY, filmId, director.getId());
            }
        });
        return getFilmDirectors(filmId);
    }
}
