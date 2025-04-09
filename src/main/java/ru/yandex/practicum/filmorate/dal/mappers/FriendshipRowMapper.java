package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();

        friendship.setUserId(resultSet.getLong("user_id"));
        friendship.setFriendId(resultSet.getLong("friend_id"));
        friendship.setFriendLogin(resultSet.getString("login"));
        friendship.setFriendShipStatus(FriendshipStatus.valueOf(resultSet.getString("friendship_status_name")));

        return friendship;
    }
}
