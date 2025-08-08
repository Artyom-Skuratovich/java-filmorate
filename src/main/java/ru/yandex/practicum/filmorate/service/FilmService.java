package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.StorageUtils;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        return FilmMapper.mapToFilmDto(film, mpa, genreStorage.findGenresForFilm(filmId));
    }

    public FilmDto create(CreateFilmRequest request) {
        Mpa mpa = StorageUtils.findModel(mpaStorage, request.getMpa().getId(), String.format(
                "MPA-рейтинг с id=%d не найден",
                request.getMpa().getId()
        ));
        checkGenres(request.getGenres());
        Film film = filmStorage.create(FilmMapper.mapToFilm(request));
        request.getGenres().forEach(g -> genreStorage.addGenreToFilm(film.getId(), g.getId()));
        return FilmMapper.mapToFilmDto(film, mpa, genreStorage.findGenresForFilm(film.getId()));
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film film = StorageUtils.findModel(filmStorage, request.getId(), String.format(
                "Фильм с id=%d не найден",
                request.getId()
        ));
        Mpa mpa = StorageUtils.findModel(mpaStorage, request.getRate(), String.format(
                "MPA-рейтинг с id=%d не найден",
                request.getRate()
        ));
        film = filmStorage.update(FilmMapper.updateFilmProperties(film, request));
        if (request.getGenres() != null) {
            updateGenresForFilm(film.getId(), request.getGenres().stream().map(Genre::getId).toList());
        }
        return FilmMapper.mapToFilmDto(film, mpa, genreStorage.findGenresForFilm(film.getId()));
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

    public List<FilmDto> findMostPopularFilms(int count) {
        return filmStorage.findMostPopularFilms(count)
                .stream()
                .map(this::buildDto)
                .toList();
    }

    private void updateGenresForFilm(int filmId, List<Integer> genres) {
        List<Integer> currentGenres = genreStorage.findGenresForFilm(filmId).stream().map(Genre::getId).toList();
        List<Integer> genresForCreate = new ArrayList<>();
        Set<Integer> genresForDelete = new HashSet<>(currentGenres);

        for (Integer genreId : genres) {
            if (!currentGenres.contains(genreId)) {
                genresForCreate.add(genreId);
            }
        }
        genres.forEach(genresForDelete::remove);
        genresForCreate.forEach(g -> genreStorage.addGenreToFilm(filmId, g));
        genresForDelete.forEach(g -> genreStorage.deleteGenreFromFilm(filmId, g));
    }

    private void checkGenres(List<Genre> genres) {
        genres.forEach(g -> StorageUtils.findModel(genreStorage, g.getId(), String.format(
                "Жанр с id=%d не найден",
                g.getId()
        )));
    }

    private FilmDto buildDto(Film film) {
        Optional<Mpa> mpa = mpaStorage.find(film.getMpaId());
        return FilmMapper.mapToFilmDto(
                film,
                mpa.orElseThrow(() -> new NotFoundException(String.format("MPA с id=%d не найден", film.getMpaId()))),
                genreStorage.findGenresForFilm(film.getId())
        );
    }
}