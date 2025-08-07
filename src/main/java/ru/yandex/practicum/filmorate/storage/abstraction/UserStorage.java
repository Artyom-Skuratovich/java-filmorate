package ru.yandex.practicum.filmorate.storage.abstraction;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends Storage<User> {
    List<User> findUnconfirmedFriendRequests(int userId);

    List<User> findCommonFriends(int firstUserId, int secondUserId);

    List<User> findFriends(int userId);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);
}