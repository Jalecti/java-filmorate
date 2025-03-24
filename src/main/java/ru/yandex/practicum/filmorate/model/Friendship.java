package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"friendId"})
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private Long friendId;
    private FriendshipStatus friendShipStatus;
}
