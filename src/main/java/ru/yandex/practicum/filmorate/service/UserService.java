package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserEventRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserEventRepository userEventRepository;

    public Collection<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(@Valid NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        User updatedUser = userRepository.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.error("Пользователь не найден с ID: {}", request.getId());
                    return new NotFoundException("Пользователь не найден с ID: " + request.getId());
                });
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public boolean delete(Long userId) {
        checkUser(userId);
        return userRepository.delete(userId);
    }

    public void addToFriends(Long userId, Long friendId) {
        checkUser(userId);
        checkUser(friendId);

        friendshipRepository.addToFriends(userId, friendId);
        userEventRepository.addUserEvent(userId, friendId, EventType.FRIEND.name(), EventOperation.ADD.name());
        log.info("Пользователи с id={} и id={} добавлены в друзья друг к другу", userId, friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        checkUser(userId);
        checkUser(friendId);

        friendshipRepository.deleteFromFriends(userId, friendId);
        userEventRepository.addUserEvent(userId, friendId, EventType.FRIEND.name(), EventOperation.REMOVE.name());
        log.info("Пользователи с id={} и id={} удалены из друзей друг у друга", userId, friendId);
    }

    public Collection<UserDto> getCommonFriends(Long userId, Long otherId) {
        checkUser(userId);
        checkUser(otherId);

        Set<UserDto> commonFriends = new HashSet<>();
        friendshipRepository.findAll(userId).forEach(friendship -> {
            Optional<User> user = userRepository.getUserById(friendship.getFriendId());
            user.ifPresent(value -> commonFriends.add(UserMapper.mapToUserDto(value)));
        });

        Set<UserDto> otherFriends = new HashSet<>();
        friendshipRepository.findAll(otherId).forEach(friendship -> {
            Optional<User> user = userRepository.getUserById(friendship.getFriendId());
            user.ifPresent(value -> otherFriends.add(UserMapper.mapToUserDto(value)));
        });
        commonFriends.retainAll(otherFriends);
        return commonFriends;
    }

    public Collection<UserDto> getUserFriends(Long userId) {
        checkUser(userId);

        Set<UserDto> userFriends = new HashSet<>();
        friendshipRepository.findAll(userId).forEach(friendship -> {
            Optional<User> user = userRepository.getUserById(friendship.getFriendId());
            user.ifPresent(value -> userFriends.add(UserMapper.mapToUserDto(value)));
        });

        return userFriends;
    }

    public UserDto getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден с ID: {}", userId);
                    return new NotFoundException("Пользователь не найден с ID: " + userId);
                });
    }

    public List<UserEvent> getUserEvents(Long userId) {
        checkUser(userId);
        return userEventRepository.findAllByUserId(userId);
    }

    public void checkUser(Long userId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь не найден с ID: {}", userId);
            throw new NotFoundException("Пользователь не найден с ID: " + userId);
        }
    }
}
