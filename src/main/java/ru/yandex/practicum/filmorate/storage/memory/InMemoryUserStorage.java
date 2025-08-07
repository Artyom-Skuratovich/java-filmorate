package ru.yandex.practicum.filmorate.storage.memory;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private static int id = 1;
    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public List<User> findUnconfirmedFriendRequests(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findCommonFriends(int firstUserId, int secondUserId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findFriends(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User create(User model) {
        model.setId(getNextId());
        users.put(model.getId(), model);
        return model;
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> find(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(User model) {
        if (!users.containsKey(model.getId())) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", model.getId()));
        }
        users.put(model.getId(), model);
        return model;
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    private static synchronized int getNextId() {
        return id++;
    }
}