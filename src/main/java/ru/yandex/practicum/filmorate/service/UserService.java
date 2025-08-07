package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.StorageUtils;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage storage;

    public List<User> findAll() {
        return storage.findAll();
    }

    public User find(int userId) {
        return StorageUtils.findModelOrThrowIfDoesNotExist(storage, userId, String.format(
                "Пользователь с id=%d не найден",
                userId
        ));
    }

    public User create(User user) {
        checkName(user);
        return storage.create(user);
    }

    public User update(User user) {
        checkName(user);
        return storage.update(user);
    }

    public void delete(int userId) {
        storage.delete(userId);
    }

    public User addFriend(int userId, int friendId) {
        User user = StorageUtils.findModelOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось добавить друга пользователю с id=%d, так как такого пользователя не существует",
                userId
        ));
        User friend = StorageUtils.findModelOrThrowIfDoesNotExist(storage, friendId, String.format(
                "Не удалось добавить друга с id=%d, так как такого пользователя не существует",
                friendId
        ));
        storage.addFriend(user.getId(), friend.getId());
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = StorageUtils.findModelOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось удалить друга у пользователя с id=%d, так как такого пользователя не существует",
                userId
        ));
        User friend = StorageUtils.findModelOrThrowIfDoesNotExist(storage, friendId, String.format(
                "Не удалось удалить друга с id=%d, так как такого пользователя не существует",
                friendId
        ));
        storage.deleteFriend(user.getId(), friend.getId());
        return user;
    }

    public List<User> findCommonFriends(int firstUserId, int secondUserId) {
        String errorMessage = "Пользователь с id=%d не найден";
        User firstUser = StorageUtils.findModelOrThrowIfDoesNotExist(
                storage,
                firstUserId,
                String.format(errorMessage, firstUserId)
        );
        User secondUser = StorageUtils.findModelOrThrowIfDoesNotExist(
                storage,
                secondUserId,
                String.format(errorMessage, secondUserId)
        );
        return storage.findCommonFriends(firstUser.getId(), secondUser.getId());
    }

    public List<User> findFriends(int userId) {
        User user = StorageUtils.findModelOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось получить список друзей для несуществующего пользователя, id=%d",
                userId
        ));
        return storage.findFriends(user.getId());
    }

    private void checkName(User user) {
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}