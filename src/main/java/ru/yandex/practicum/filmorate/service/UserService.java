package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static int id = 1;
    private final Map<Integer, User> users;

    public UserService() {
        users = new HashMap<>();
    }

    public List<User> getAll() {
        return users.values().stream().toList();
    }

    public User create(User user) {
        checkName(user);
        user.setId(nextId());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        checkName(user);
        users.put(user.getId(), user);
        return user;
    }

    private static void checkName(User user) {
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private static synchronized int nextId() {
        return id++;
    }
}