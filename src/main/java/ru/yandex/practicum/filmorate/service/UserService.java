package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public List<User> getAll() {
        return storage.getAll();
    }

    public Optional<User> get(int userId) {
        return storage.get(userId);
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
        User user = Storages.getUserOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось добавить друга пользователю с id=%d, так как такого пользователя не существует",
                userId
        ));
        User friend = Storages.getUserOrThrowIfDoesNotExist(storage, friendId, String.format(
                "Не удалось добавить друга с id=%d, так как такого пользователя не существует",
                friendId
        ));
        friend.getFriends().add(userId);
        storage.update(friend);
        user.getFriends().add(friendId);
        return storage.update(user);
    }

    public User removeFriend(int userId, int friendId) {
        User user = Storages.getUserOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось удалить друга у пользователя с id=%d, так как такого пользователя не существует",
                userId
        ));
        User friend = Storages.getUserOrThrowIfDoesNotExist(storage, friendId, String.format(
                "Не удалось удалить друга с id=%d, так как такого пользователя не существует",
                friendId
        ));
        friend.getFriends().remove(userId);
        storage.update(friend);
        user.getFriends().remove(friendId);
        return storage.update(user);
    }

    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        String errorMessage = "Пользователь с id=%d не найден";
        User firstUser = Storages.getUserOrThrowIfDoesNotExist(
                storage,
                firstUserId,
                String.format(errorMessage, firstUserId)
        );
        User secondUser = Storages.getUserOrThrowIfDoesNotExist(
                storage,
                secondUserId,
                String.format(errorMessage, secondUserId)
        );

        Set<Integer> commonFriends = new HashSet<>(firstUser.getFriends());
        commonFriends.retainAll(secondUser.getFriends());
        return getUsersFromIds(commonFriends);
    }

    public List<User> getFriends(int userId) {
        User user = Storages.getUserOrThrowIfDoesNotExist(storage, userId, String.format(
                "Не удалось получить список друзей для несуществующего пользователя, id=%d",
                userId
        ));
        return getUsersFromIds(user.getFriends());
    }

    private void checkName(User user) {
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private List<User> getUsersFromIds(Collection<Integer> ids) {
        return ids.stream().map(storage::get).flatMap(Optional::stream).toList();
    }
}