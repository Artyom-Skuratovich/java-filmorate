package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final FilmService filmService;

    @PostMapping
    public User create(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST-запрос на создание пользователя");
        User created = service.create(request);
        log.info("Пользователь успешно создан, id={}", created.getId());
        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT-запрос на обновление пользователя, id={}", request.getId());
        User updated = service.update(request);
        log.info("Пользователь с id={} успешно обновлён", updated.getId());
        return updated;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET-запрос на получение списка всех пользователей");
        return service.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable int userId) {
        log.info("GET-запрос на получение пользователя, id={}", userId);
        return service.find(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void delete(@PathVariable int userId) {
        log.info("DELETE-запрос на удаление пользователя, id={}", userId);
        service.delete(userId); // <-- используем поле service
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("PUT-запрос на добавление друга, userId={}, friendId={}", userId, friendId);
        return service.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        log.info("DELETE-запрос на удаление друга, userId={}, friendId={}", userId, friendId);
        return service.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> findFriends(@PathVariable int userId) {
        log.info("GET-запрос на получение списка друзей, userId={}", userId);
        return service.findFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("GET-запрос на получение списка общих друзей, userId={}, otherId={}", userId, otherId);
        return service.findCommonFriends(userId, otherId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<FilmDto> findFilmRecommendations(@PathVariable int userId) {
        log.info("GET-запрос на получение списка рекомендаций по фильмам для пользователя userId={}", userId);
        return filmService.findFilmRecommendations(userId);
    }
}