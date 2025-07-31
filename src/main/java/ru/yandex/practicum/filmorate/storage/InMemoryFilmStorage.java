package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.abstraction.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static int id = 1;
    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public List<Film> getAll() {
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> get(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(String.format("Фильм с id=%d не найден", film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        films.remove(id);
    }

    private static synchronized int getNextId() {
        return id++;
    }
}