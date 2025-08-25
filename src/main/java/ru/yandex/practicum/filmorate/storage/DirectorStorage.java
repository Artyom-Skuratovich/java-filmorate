package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage extends Storage<Director> {
    List<Director> findDirectorsForFilm(int filmId);

    void addDirectorToFilm(int filmId, int directorId);

    void deleteDirectorFromFilm(int filmId, int directorId);
}