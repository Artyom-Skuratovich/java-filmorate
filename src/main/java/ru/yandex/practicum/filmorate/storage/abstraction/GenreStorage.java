package ru.yandex.practicum.filmorate.storage.abstraction;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage extends Storage<Genre> {
    List<Genre> findGenresForFilm(int filmId);

    void addGenreToFilm(int filmId, int genreId);

    void deleteGenreFromFilm(int filmId, int genreId);
}