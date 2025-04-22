package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repositories.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.repositories.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FilmRepository filmRepository;

    @Autowired
    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository, FilmRepository filmRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.filmRepository = filmRepository;
    }

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
        log.info("Пользователи с id={} и id={} добавлены в друзья друг к другу", userId, friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        checkUser(userId);
        checkUser(friendId);

        friendshipRepository.deleteFromFriends(userId, friendId);
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

    public void checkUser(Long userId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Пользователь не найден с ID: {}", userId);
            throw new NotFoundException("Пользователь не найден с ID: " + userId);
        }
    }

    public Collection<Film> getRecommendationsForUser(Long userId) {
        Optional<User> user = userRepository.getUserById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }

        Integer likeCount = filmRepository.getCountLikes(userId);

        if (likeCount == 0) {
            return Collections.emptyList();
        }

        Collection<Film> popularFilms = filmRepository.findMostPopular(likeCount);

        return Collections.singletonList(popularFilms.iterator().next());
    }
}
