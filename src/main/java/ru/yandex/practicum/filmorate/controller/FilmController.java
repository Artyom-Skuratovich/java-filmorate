package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmSearchOption;
import ru.yandex.practicum.filmorate.storage.FilmSortOption;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping
    public FilmDto create(@Valid @RequestBody CreateFilmRequest request) {
        log.info("POST-запрос на создание фильма");
        FilmDto created = service.create(request);
        log.info("Фильм успешно создан, id={}", created.getId());
        return created;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("PUT-запрос на обновление фильма, id={}", request.getId());
        FilmDto updated = service.update(request);
        log.info("Фильм с id={} успешно обновлён", updated.getId());
        return updated;
    }

    @GetMapping
    public List<FilmDto> findAll() {
        log.info("GET-запрос на получение списка всех фильмов");
        return service.findAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto find(@PathVariable int filmId) {
        log.info("GET-запрос на получение фильма, id={}", filmId);
        return service.find(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable int filmId) {
        log.info("DELETE-запрос на удаление фильма, id={}", filmId);
        service.delete(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public FilmDto addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("PUT-запрос на добавление лайка фильму, filmId={}, userId={}", filmId, userId);
        return service.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public FilmDto deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("DELETE-запрос на удаление лайка фильма, filmId={}, userId={}", filmId, userId);
        return service.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> findMostPopularFilms(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.info("GET-запрос на получение популярных фильмов, count={}, genreId={}, year={}",
                count, genreId, year);
        return service.findMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<FilmDto> findCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("GET-запрос на получение общих фильмов пользователей, userId={}, friendId={}",
                userId, friendId);
        return service.findCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> findDirectorFilmsSorted(
            @PathVariable int directorId,
            @RequestParam(name = "sortBy") FilmSortOption sortOption) {
        log.info(
                "GET-запрос на получение фильмов реж. сорт. по кол-ву лайков или г. вып/, directorId={}, sortOption={}",
                directorId,
                sortOption
        );
        return service.findDirectorFilmsSorted(directorId, sortOption);
    }

    @GetMapping("/search")
    public List<FilmDto> search(
            @RequestParam(name = "query") String pattern,
            @RequestParam(name = "by") FilmSearchOption searchOption) {
        log.info("GET-запрос на поиск по названию фильмов и по режиссёру, pattern={}, by={}", pattern, searchOption);
        return service.search(pattern, searchOption);
    }
}