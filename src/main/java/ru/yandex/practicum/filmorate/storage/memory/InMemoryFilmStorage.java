package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmSearchOption;
import ru.yandex.practicum.filmorate.storage.FilmSortOption;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
@Service
public class InMemoryFilmStorage implements FilmStorage {
    private static int id = 1;
    private final Map<Integer, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public List<Film> findMostPopularFilms(int count, Integer genreId, Integer year) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addLike(int filmId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Film create(Film model) {
        model.setId(getNextId());
        films.put(model.getId(), model);
        return model;
    }

    @Override
    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> find(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film update(Film model) {
        if (!films.containsKey(model.getId())) {
            throw new NotFoundException(String.format("Фильм с id=%d не найден", model.getId()));
        }
        films.put(model.getId(), model);
        return model;
    }

    @Override
    public void delete(int id) {
        films.remove(id);
    }

    @Override
    public List<Film> findFilmRecommendations(int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> findDirectorFilmsSorted(int directorId, FilmSortOption sortOption) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> search(String pattern, FilmSearchOption searchOption) {
        throw new UnsupportedOperationException();
    }

    private static synchronized int getNextId() {
        return id++;
    }
}