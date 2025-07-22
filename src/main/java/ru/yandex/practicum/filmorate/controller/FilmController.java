package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Film created = service.create(film);
        log.info("Фильм успешно создан, id={}", film.getId());

        return created;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film updated = service.update(film);
        log.info("Фильм с id={} успешно обновлён", film.getId());

        return updated;
    }

    @GetMapping
    public List<Film> get() {
        return service.getAll();
    }
}