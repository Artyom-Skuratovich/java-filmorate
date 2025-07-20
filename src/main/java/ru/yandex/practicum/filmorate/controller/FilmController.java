package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int currentId = 1;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан, id={}", film.getId());

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            String message = "Фильм с id=" + film.getId() + " не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
        validate(film);
        films.put(film.getId(), film);
        log.info("Фильм с id={} успешно обновлён", film.getId());

        return film;
    }

    @GetMapping
    public Collection<Film> get() {
        return films.values();
    }

    private static synchronized int getNextId() {
        return currentId++;
    }

    private static void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String message = "Дата релиза должна быть не раньше 28 декабря 1895 года";
            log.warn(message);
            throw new ValidationException(message);
        }
    }
}