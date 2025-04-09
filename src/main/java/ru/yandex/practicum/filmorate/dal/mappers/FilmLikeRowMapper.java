package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikeRowMapper implements RowMapper<FilmLike> {
    @Override
    public FilmLike mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FilmLike filmLike = new FilmLike();
        filmLike.setFilmId(resultSet.getLong("film_id"));
        filmLike.setUserId(resultSet.getLong("user_id"));

        return filmLike;
    }
}
