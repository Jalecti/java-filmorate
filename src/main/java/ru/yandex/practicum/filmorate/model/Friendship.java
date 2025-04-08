package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"userId", "friendId"})
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private Long userId;
    private Long friendId;
    private String friendLogin;
    private FriendshipStatus friendShipStatus;
}
