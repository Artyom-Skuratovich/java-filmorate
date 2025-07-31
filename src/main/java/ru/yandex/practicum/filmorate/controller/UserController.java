package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST-запрос на создание пользователя");
        User created = service.create(user);
        log.info("Пользователь успешно создан, id={}", user.getId());
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("PUT-запрос на обновление пользователя, id={}", user.getId());
        User updated = service.update(user);
        log.info("Пользователь с id={} успешно обновлён", user.getId());
        return updated;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET-запрос на получение списка всех пользователей");
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable int userId) {
        log.info("GET-запрос на получение пользователя, id={}", userId);
        return service.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        log.info("DELETE-запрос на удаление пользователя, id={}", userId);
        service.delete(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("PUT-запрос на добавление друга, userId={}, friendId={}", userId, friendId);
        return service.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("DELETE-запрос на удаление друга, userId={}, friendId={}", userId, friendId);
        return service.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        log.info("GET-запрос на получение списка друзей, userId={}", userId);
        return service.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("GET-запрос на получение списка общих друзей, userId={}, otherId={}", userId, otherId);
        return service.getCommonFriends(userId, otherId);
    }
}