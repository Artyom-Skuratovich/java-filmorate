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
        User created = service.create(user);
        log.info("Пользователь успешно создан, id={}", user.getId());

        return created;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User updated = service.update(user);
        log.info("Пользователь с id={} успешно обновлён", user.getId());

        return updated;
    }

    @GetMapping
    public List<User> get() {
        return service.getAll();
    }
}