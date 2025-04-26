package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<UserDto> getUserFriends(@PathVariable Long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public Collection<UserDto> getCommonFriends(@PathVariable("userId") Long userId,
                                                @PathVariable("friendId") Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @GetMapping("/{userId}/feed")
    public Collection<UserEvent> getUserEvents(@PathVariable("userId") Long userId) {
        return userService.getUserEvents(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest newUserRequest) {
        return userService.update(newUserRequest);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addToFriends(@PathVariable("userId") @NotNull Long userId,
                             @PathVariable("friendId") @NotNull Long friendId) {
        userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean delete(@PathVariable Long userId) {
        return userService.delete(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("userId") Long userId,
                                  @PathVariable("friendId") Long friendId) {
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/{userId}/recommendations")
    public Collection<FilmDto> getRecommendations(@PathVariable("userId") Long id) {
        return recommendationService.getRecommendationsForUser(id);
    }

}
