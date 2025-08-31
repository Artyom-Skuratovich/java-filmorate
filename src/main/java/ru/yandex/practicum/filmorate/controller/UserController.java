package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    @PostMapping
    public User create(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST-запрос на создание пользователя");
        User created = userService.create(request);
        log.info("Пользователь успешно создан, id={}", created.getId());
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT-запрос на обновление пользователя, id={}", request.getId());
        User updated = userService.update(request);
        log.info("Пользователь с id={} успешно обновлён", updated.getId());
        return updated;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET-запрос на получение списка всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable int userId) {
        log.info("GET-запрос на получение пользователя, id={}", userId);
        return userService.find(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        log.info("DELETE-запрос на удаление пользователя, id={}", userId);
        userService.delete(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("PUT-запрос на добавление друга, userId={}, friendId={}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("DELETE-запрос на удаление друга, userId={}, friendId={}", userId, friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> findFriends(@PathVariable int userId) {
        log.info("GET-запрос на получение списка друзей, userId={}", userId);
        return userService.findFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("GET-запрос на получение списка общих друзей, userId={}, otherId={}", userId, otherId);
        return userService.findCommonFriends(userId, otherId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<FilmDto> findFilmRecommendations(@PathVariable int userId) {
        log.info("GET-запрос на получение списка рекомендаций по фильмам для пользователя userId={}", userId);
        return filmService.findFilmRecommendations(userId);
    }

    @GetMapping("/{userId}/feed")
    public List<Event> findEvents(@PathVariable int userId) {
        log.info("GET-запрос на получение событий пользователя, id={}", userId);
        return eventService.findForUser(userId);
    }
}