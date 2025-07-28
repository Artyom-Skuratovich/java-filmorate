package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.abstraction.FilmStorage;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film get(int filmId) {
        return Storages.getFilmOrThrowIfDoesNotExist(filmStorage, filmId, String.format(
                "Фильм с id=%d не найден",
                filmId
        ));
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(int filmId) {
        filmStorage.delete(filmId);
    }

    public Film addLike(int filmId, int userId) {
        Film film = Storages.getFilmOrThrowIfDoesNotExist(filmStorage, filmId, String.format(
                "Не удалось поставить лайк фильму с id=%d, так как такого фильма не существует",
                filmId
        ));
        User user = Storages.getUserOrThrowIfDoesNotExist(userStorage, userId, String.format(
                "Не удалось поставить лайк фильму, так как пользователя с id=%d не существует",
                userId
        ));
        film.getLikes().add(user.getId());
        return filmStorage.update(film);
    }

    public Film removeLike(int filmId, int userId) {
        Film film = Storages.getFilmOrThrowIfDoesNotExist(filmStorage, filmId, String.format(
                "Не удалось поставить лайк фильму с id=%d, так как такого фильма не существует",
                filmId
        ));
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException(String.format(
                    "Не удалось удалить лайк фильму с id=%d, так как пользователя с id=%d не существует",
                    filmId,
                    userId
            ));
        }
        film.getLikes().remove(userId);
        return filmStorage.update(film);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(f -> ((Film) f).getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}