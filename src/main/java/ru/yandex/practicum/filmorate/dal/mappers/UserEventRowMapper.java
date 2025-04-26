package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserEventRowMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent userEvent = new UserEvent();

        userEvent.setEventId(rs.getLong("event_id"));
        userEvent.setUserId(rs.getLong("user_id"));
        userEvent.setEventType(EventType.valueOf(rs.getString("uet_name")));
        userEvent.setOperation(EventOperation.valueOf(rs.getString("eo_name")));
        userEvent.setEntityId(rs.getLong("entity_id"));
        userEvent.setTimestamp(rs.getTimestamp("event_timestamp").toInstant().toEpochMilli());

        return userEvent;
    }
}
