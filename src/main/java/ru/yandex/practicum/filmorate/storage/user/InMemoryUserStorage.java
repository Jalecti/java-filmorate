package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long counter = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        processUserName(user);
        user.setId(++counter);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);

        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        processUserName(newUser);

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.info("Обновление пользователя: {}", oldUser);
            log.info("Информация для обновления: {}", newUser);

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());

            log.info("Пользователь обновлен: {}", oldUser);
            return oldUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User delete(Long userId) {
        checkUser(userId);
        User userToDelete = users.get(userId);
        users.remove(userId);
        log.info("Пользователь с id={} удален", userId);
        return userToDelete;
    }

    @Override
    public void checkUser(Long userId) {
        if (userId == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(userId)) {
            log.error("Пользователь с id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }

    @Override
    public User getUserById(Long userId) {
        checkUser(userId);
        return users.get(userId);
    }

    private void processUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
