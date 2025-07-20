package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int currentId = 1;

    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        checkName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан, id={}", user.getId());

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            String message = "Пользователь с id=" + user.getId() + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        checkName(user);
        users.put(user.getId(), user);
        log.info("Пользователь с id={} успешно обновлён", user.getId());

        return user;
    }

    @GetMapping
    public Collection<User> get() {
        return users.values();
    }

    private static synchronized int getNextId() {
        return currentId++;
    }

    private static void checkName(User user) {
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}