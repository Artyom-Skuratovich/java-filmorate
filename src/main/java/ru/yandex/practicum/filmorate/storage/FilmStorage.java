package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    List<Film> findMostPopularFilms(int count);

    void addLike(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);

    // из add-feed: удаление фильма по id
    void delete(int filmId);

    // из develop: рекомендации фильмов
    List<Film> findFilmRecommendations(int userId);
}