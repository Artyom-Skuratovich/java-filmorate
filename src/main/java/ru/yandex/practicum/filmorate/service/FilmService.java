package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.abstraction.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }
}