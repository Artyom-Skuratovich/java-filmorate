package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmSortOption;
import ru.yandex.practicum.filmorate.dto.create.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SameUserIdsException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    public List<FilmDto> findAll() {
        return filmStorage.findAll()
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public FilmDto find(int filmId) {
        Film film = StorageUtils.findModel(filmStorage, filmId, String.format(
                "Фильм с id=%d не найден",
                filmId
        ));
        Mpa mpa = StorageUtils.findModel(mpaStorage, film.getMpaId(), String.format(
                "MPA-рейтинг с id=%d не найден",
                film.getMpaId()
        ));
        return FilmMapper.mapToFilmDto(
                film,
                mpa,
                genreStorage.findGenresForFilm(filmId),
                directorStorage.findDirectorsForFilm(filmId)
        );
    }

    public FilmDto create(CreateFilmRequest request) {
        Mpa mpa = StorageUtils.findModel(mpaStorage, request.getMpa().getId(), String.format(
                "MPA-рейтинг с id=%d не найден",
                request.getMpa().getId()
        ));
        checkGenres(request.getGenres());
        Film film = filmStorage.create(FilmMapper.mapToFilm(request));
        if (request.getGenres() != null) {
            request.getGenres().forEach(g -> genreStorage.addGenreToFilm(film.getId(), g.getId()));
        }
        if (request.getDirectors() != null) {
            request.getDirectors().forEach(d -> directorStorage.addDirectorToFilm(film.getId(), d.getId()));
        }
        return FilmMapper.mapToFilmDto(
                film,
                mpa,
                genreStorage.findGenresForFilm(film.getId()),
                directorStorage.findDirectorsForFilm(film.getId())
        );
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film film = StorageUtils.findModel(filmStorage, request.getId(), String.format(
                "Фильм с id=%d не найден",
                request.getId()
        ));
        Mpa mpa = StorageUtils.findModel(mpaStorage, request.getMpa().getId(), String.format(
                "MPA-рейтинг с id=%d не найден",
                request.getMpa().getId()
        ));
        film = filmStorage.update(FilmMapper.updateFilmProperties(film, request));
        if (request.getGenres() != null) {
            updateGenresForFilm(film.getId(), request.getGenres());
        }
        if (request.getDirectors() != null) {
            updateDirectorsForFilm(film.getId(), request.getDirectors());
        }
        return FilmMapper.mapToFilmDto(
                film,
                mpa,
                genreStorage.findGenresForFilm(film.getId()),
                directorStorage.findDirectorsForFilm(film.getId())
        );
    }

    public void delete(int filmId) {
        filmStorage.delete(filmId);
    }

    public FilmDto addLike(int filmId, int userId) {
        Film film = StorageUtils.findModel(filmStorage, filmId, String.format(
                "Не удалось поставить лайк фильму с id=%d, так как такого фильма не существует",
                filmId
        ));
        User user = StorageUtils.findModel(userStorage, userId, String.format(
                "Не удалось поставить лайк фильму, так как пользователя с id=%d не существует",
                userId
        ));
        filmStorage.addLike(film.getId(), user.getId());
        return buildDto(film);
    }

    public FilmDto deleteLike(int filmId, int userId) {
        Film film = StorageUtils.findModel(filmStorage, filmId, String.format(
                "Не удалось поставить лайк фильму с id=%d, так как такого фильма не существует",
                filmId
        ));
        if (!filmStorage.deleteLike(filmId, userId)) {
            throw new NotFoundException(String.format(
                    "Не удалось удалить лайк фильму с id=%d, так как пользователь с id=%d не ставил лайк",
                    filmId,
                    userId
            ));
        }
        return buildDto(film);
    }

    public List<FilmDto> findMostPopularFilms(int count, Integer genreId, Integer year) {
        if (genreId != null) {
            StorageUtils.findModel(genreStorage, genreId, String.format(
                    "Жанр с id=%d не найден", genreId
            ));
        }
        List<Film> films = filmStorage.findMostPopularFilms(count, genreId, year);
        return films.stream()
                .map(this::buildDto)
                .toList();
    }

    public List<FilmDto> findFilmRecommendations(int userId) {
        User user = StorageUtils.findModel(userStorage, userId, String.format(
                "Не удалось получить рекомендации по фильмам для несуществующего пользователя, id=%d",
                userId
        ));
        return filmStorage.findFilmRecommendations(user.getId())
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public List<FilmDto> findCommonFilms(int userId, int friendId) {
        StorageUtils.findModel(userStorage, userId, String.format(
                "Пользователь с id=%d не найден", userId
        ));
        StorageUtils.findModel(userStorage, friendId, String.format(
                "Пользователь с id=%d не найден", friendId
        ));
        if (userId == friendId) {
            throw new SameUserIdsException("userId и friendId не могут быть одинаковыми");
        }
        List<Film> commonFilms = filmStorage.findCommonFilms(userId, friendId);
        return commonFilms.stream()
                .map(this::buildDto)
                .toList();
    }

    public List<FilmDto> findDirectorFilmsSorted(int directorId, FilmSortOption sortOption) {
        StorageUtils.findModel(directorStorage, directorId, String.format(
                "Режиссёр с id=%d не найден",
                directorId
        ));
        return filmStorage.findDirectorFilmsSorted(directorId, sortOption)
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public List<FilmDto> search(String pattern, FilmSearchOption searchOption) {
        return filmStorage.search(pattern, searchOption)
                .stream()
                .map(this::buildDto)
                .toList();
    }

    private void updateGenresForFilm(int filmId, Set<Genre> genres) {
        List<Integer> current = genreStorage.findGenresForFilm(filmId).stream().map(Genre::getId).toList();
        List<Integer> forCreate = new ArrayList<>();
        Set<Integer> forDelete = new HashSet<>(current);

        genres.forEach(g -> {
            if (!current.contains(g.getId())) {
                forCreate.add(g.getId());
            }
            forDelete.remove(g.getId());
        });

        forCreate.forEach(g -> genreStorage.addGenreToFilm(filmId, g));
        forDelete.forEach(g -> genreStorage.deleteGenreFromFilm(filmId, g));
    }

    private void updateDirectorsForFilm(int filmId, Set<Director> directors) {
        List<Integer> current = directorStorage.findDirectorsForFilm(filmId).stream().map(Director::getId).toList();
        List<Integer> forCreate = new ArrayList<>();
        Set<Integer> forDelete = new HashSet<>(current);

        directors.forEach(d -> {
            if (!current.contains(d.getId())) {
                forCreate.add(d.getId());
            }
            forDelete.remove(d.getId());
        });

        forCreate.forEach(d -> directorStorage.addDirectorToFilm(filmId, d));
        forDelete.forEach(d -> directorStorage.deleteDirectorFromFilm(filmId, d));
    }

    private void checkGenres(Set<Genre> genres) {
        if (genres != null) {
            genres.forEach(g -> StorageUtils.findModel(genreStorage, g.getId(), String.format(
                    "Жанр с id=%d не найден",
                    g.getId()
            )));
        }
    }

    private FilmDto buildDto(Film film) {
        Optional<Mpa> mpa = mpaStorage.find(film.getMpaId());
        return FilmMapper.mapToFilmDto(
                film,
                mpa.orElseThrow(() -> new NotFoundException(String.format("MPA с id=%d не найден", film.getMpaId()))),
                genreStorage.findGenresForFilm(film.getId()),
                directorStorage.findDirectorsForFilm(film.getId())
        );
    }
}