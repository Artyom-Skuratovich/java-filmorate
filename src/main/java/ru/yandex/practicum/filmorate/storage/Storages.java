package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.abstraction.FilmStorage;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.Optional;

public final class Storages {
    private Storages() {
    }

    public static User getUserOrThrowIfDoesNotExist(UserStorage storage, int userId, String errorMessage) {
        Optional<User> optionalUser = storage.get(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(errorMessage);
        }
        return optionalUser.get();
    }

    public static Film getFilmOrThrowIfDoesNotExist(FilmStorage storage, int filmId, String errorMessage) {
        Optional<Film> optionalFilm = storage.get(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException(errorMessage);
        }
        return optionalFilm.get();
    }
}