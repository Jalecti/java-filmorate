package ru.yandex.practicum.filmorate.dal.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Slf4j
public class UserEventRepository extends BaseRepository<UserEvent> {
    private static final String FIND_ALL_BY_USER_ID =
            "SELECT ue.event_id, " +
                    "ue.user_id, " +
                    "uet.uet_name, " +
                    "eo_name, " +
                    "ue.entity_id, " +
                    "ue.event_timestamp " +
                    "FROM user_events AS ue " +
                    "INNER JOIN user_event_types AS uet ON ue.event_type_id = uet.type_id " +
                    "INNER JOIN event_operations AS eo ON ue.operation_id = eo.operation_id " +
                    "WHERE user_id = ?";

    private static final String INSERT_QUERY =
            "INSERT INTO user_events (user_id, event_type_id, operation_id, entity_id) " +
                    "SELECT ?, uet.type_id, eo.operation_id, ? " +
                    "FROM user_event_types AS uet, event_operations AS eo " +
                    "WHERE uet.uet_name = ? AND eo.eo_name = ?";

    public UserEventRepository(JdbcTemplate jdbc, RowMapper<UserEvent> mapper) {
        super(jdbc, mapper);
    }

    public List<UserEvent> findAllByUserId(Long userId) {
        return findMany(FIND_ALL_BY_USER_ID, userId);
    }

    public void addUserEvent(Long userId, Long entityId,
                             String eventType, String eventOperation) {
        Object[] params = {userId, entityId, eventType, eventOperation};
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"event_id"});
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
