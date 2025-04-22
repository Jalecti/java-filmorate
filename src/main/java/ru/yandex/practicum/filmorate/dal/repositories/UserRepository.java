package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM users " +
                    "ORDER BY user_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM users " +
                    "WHERE user_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO users(email, login, user_name, birthday) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE users SET " +
                    "email = ?, " +
                    "login = ?, " +
                    "user_name = ?, " +
                    "birthday = ? " +
                    "WHERE user_id = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM users " +
                    "WHERE user_id = ?";

    private static final String DELETE_ALL_QUERY =
            "DELETE FROM users; " +
                    "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getUserById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public boolean delete(Long userId) {
        return delete(DELETE_QUERY, userId);
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY);
    }

    public List<Long> findUsersByLikedFilmIds(Collection<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        String sql = "SELECT DISTINCT user_id FROM users_film_likes WHERE film_id IN (" +
                String.join(",", Collections.nCopies(filmIds.size(), "?")) + ")";

        Object[] params = filmIds.toArray();

        return jdbc.queryForList(sql, params, Long.class);
    }
}
