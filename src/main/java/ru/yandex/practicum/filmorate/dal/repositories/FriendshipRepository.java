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
            "SELECT fs.user_id, " +
                    "fs.friend_id, " +
                    "u.login, " +
                    "fss.friendship_status_name " +
                    "FROM friendships AS fs " +
                    "INNER JOIN friendship_statuses AS fss ON fs.status_id = fss.status_id " +
                    "INNER JOIN users AS u ON fs.friend_id = u.user_id " +
                    "WHERE fs.user_id = ? AND fs.friend_id = ?";

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

    public Collection<Friendship> findAll(Long userId) {
        return findMany(FIND_ALL_QUERY, userId);
    }

    public Optional<Friendship> findFriendshipById(Long userId, Long friendId) {
        return findOne(FIND_BY_ID_QUERY, userId, friendId);
    }

    public void addToFriends(Long userId, Long friendId) {
        Optional<Friendship> unconfirmedFriendship = findFriendshipById(friendId, userId);
        FriendshipStatus friendshipStatus = FriendshipStatus.UNCONFIRMED;
        if (unconfirmedFriendship.isPresent()) {
            friendshipStatus = FriendshipStatus.CONFIRMED;
        }
        Integer statusId = jdbc.queryForObject(GET_FRIENDSHIP_STATUS_ID_QUERY, Integer.class, friendshipStatus.toString());
        jdbc.update(ADD_FRIENDS_QUERY, userId, friendId, statusId);
        jdbc.update(UPDATE_FRIENDS_QUERY, statusId, friendId, userId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        jdbc.update(DELETE_FRIENDS_QUERY, userId, friendId);
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY);
    }
}
