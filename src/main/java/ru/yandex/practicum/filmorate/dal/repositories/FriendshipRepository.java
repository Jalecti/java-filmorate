package ru.yandex.practicum.filmorate.dal.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {
    private static final String FIND_ALL_QUERY =
            "SELECT fs.user_id, fs.friend_id, u.login, fss.friendship_status_name " +
                    "FROM friendships AS fs " +
                    "INNER JOIN users AS u ON fs.friend_id = u.user_id " +
                    "INNER JOIN friendship_statuses AS fss ON fs.status_id = fss.status_id " +
                    "WHERE fs.user_id = ?";

    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM friendships " +
                    "WHERE user_id = ? AND friend_id = ?";

    private static final String ADD_FRIENDS_QUERY =
            "INSERT INTO friendships(user_id, friend_id, status_id) " +
                    "VALUES (?, ?, ?)";

    private static final String DELETE_FRIENDS_QUERY =
            "DELETE FROM friendships " +
                    "WHERE user_id = ? AND friend_id = ?";

    private static final String UPDATE_FRIENDS_QUERY =
            "UPDATE friendships SET status_id = ? " +
                    "WHERE user_id = ? AND friend_id = ?";

    private static final String GET_FRIENDSHIP_STATUS_ID_QUERY =
            "SELECT status_id FROM friendship_statuses " +
                    "WHERE friendship_status_name = ?";

    private static final String DELETE_ALL_QUERY =
            "DELETE FROM friendships";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Friendship> findAll(Long user_id) {
        return findMany(FIND_ALL_QUERY, user_id);
    }

    public Optional<Friendship> findFriendshipById(Long userId, Long friendId) {
        return findOne(FIND_BY_ID_QUERY, userId, friendId);
    }

    public void addToFriends(Long user_id, Long friend_id) {
        Optional<Friendship> unconfirmedFriendship = findFriendshipById(friend_id, user_id);
        FriendshipStatus friendshipStatus = FriendshipStatus.UNCONFIRMED;
        if (unconfirmedFriendship.isPresent()) {
            friendshipStatus = FriendshipStatus.CONFIRMED;
        }
        Integer status_id = jdbc.queryForObject(GET_FRIENDSHIP_STATUS_ID_QUERY, Integer.class, friendshipStatus.toString());
        jdbc.update(ADD_FRIENDS_QUERY, user_id, friend_id, status_id);
        jdbc.update(UPDATE_FRIENDS_QUERY, status_id, friend_id, user_id);
    }

    public void deleteFromFriends(Long user_id, Long friend_id) {
        jdbc.update(DELETE_FRIENDS_QUERY, user_id, friend_id);
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY);
    }
}
