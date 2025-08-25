package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    List<Film> findMostPopularFilms(int count, Integer genreId, Integer year);

    void addLike(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);

    List<Film> findCommonFilms(int userId, int friendId);

    List<Film> findFilmRecommendations(int userId);

    List<Film> findDirectorFilmsSorted(int directorId, FilmSortOption sortOption);

    List<Film> search(String pattern, FilmSearchOption searchOption);
}