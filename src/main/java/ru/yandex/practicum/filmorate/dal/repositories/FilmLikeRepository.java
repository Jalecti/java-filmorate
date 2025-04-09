package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.List;

@Repository
public class FilmLikeRepository extends BaseRepository<FilmLike> {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM users_film_likes";

    public FilmLikeRepository(JdbcTemplate jdbc, RowMapper<FilmLike> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmLike> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

}
