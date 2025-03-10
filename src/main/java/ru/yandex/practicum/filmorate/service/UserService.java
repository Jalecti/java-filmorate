package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User delete(Long userId) {
        return userStorage.delete(userId);
    }

    public void addToFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getUserFriends().add(friendId);
        friend.getUserFriends().add(userId);

        log.info("Пользователи с id={} и id={} добавлены в друзья друг к другу", userId, friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getUserFriends().remove(friendId);
        friend.getUserFriends().remove(userId);

        log.info("Пользователи с id={} и id={} удалены из друзей друг у друга", userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(otherId);

        Set<Long> commonFriendsId = new HashSet<>(user.getUserFriends());
        commonFriendsId.retainAll(friend.getUserFriends());

        Set<User> commonFriendsUsers = new HashSet<>();
        commonFriendsId.forEach(uId -> commonFriendsUsers.add(userStorage.getUserById(uId)));

        return commonFriendsUsers;
    }

    public Collection<User> getUserFriends(Long userId) {
        Set<User> userFriends = new HashSet<>();
        userStorage.getUserById(userId).getUserFriends().forEach(uId -> userFriends.add(userStorage.getUserById(uId)));
        return userFriends;
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }
}
