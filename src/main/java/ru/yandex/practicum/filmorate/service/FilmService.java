package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private static int id = 1;
    private final Map<Integer, Film> films;

    public FilmService() {
        films = new HashMap<>();
    }

    public List<Film> getAll() {
        return films.values().stream().toList();
    }

    public Film create(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);

        return film;
    }

    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        films.put(film.getId(), film);

        return film;
    }

    private static synchronized int nextId() {
        return id++;
    }
}