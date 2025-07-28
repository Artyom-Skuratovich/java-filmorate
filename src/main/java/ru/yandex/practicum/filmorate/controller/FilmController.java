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
        log.info("POST-запрос на создание фильма");
        Film created = service.create(film);
        log.info("Фильм успешно создан, id={}", film.getId());
        return created;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT-запрос на обновление фильма, id={}", film.getId());
        Film updated = service.update(film);
        log.info("Фильм с id={} успешно обновлён", film.getId());
        return updated;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET-запрос на получение списка всех фильмов");
        return service.getAll();
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable int filmId) {
        log.info("GET-запрос на получение фильма, id={}", filmId);
        return service.get(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable int filmId) {
        log.info("DELETE-запрос на удаление фильма, id={}", filmId);
        service.delete(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("PUT-запрос на добавление лайка фильму, filmId={}, userId={}", filmId, userId);
        return service.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("DELETE-запрос на удаление лайка фильма, filmId={}, userId={}", filmId, userId);
        return service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(required = false) Integer count) {
        if ((count == null) || (count < 0)) {
            count = 10;
        }
        log.info("GET-запрос на получение списка наиболее популярных фильмов, count={}", count);
        return service.getMostPopularFilms(count);
    }
}