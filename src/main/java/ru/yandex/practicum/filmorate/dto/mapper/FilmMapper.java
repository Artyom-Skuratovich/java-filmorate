package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public final class FilmMapper {
    private FilmMapper() {
    }

    public static FilmDto mapToFilmDto(Film film, Mpa mpa, List<Genre> genres) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setDuration(film.getDuration());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setMpa(mpa);
        dto.setGenres(genres);

        return dto;
    }

    public static Film mapToFilm(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setMpaId(request.getMpa().getId());
        film.setDuration(request.getDuration());
        film.setReleaseDate(request.getReleaseDate());

        return film;
    }

    public static Film updateFilmProperties(Film film, UpdateFilmRequest request) {
        if (request.getName() != null) {
            film.setName(request.getName());
        }
        if (request.getDescription() != null) {
            film.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            film.setDuration(request.getDuration());
        }
        if (request.getRate() != null) {
            film.setMpaId(request.getRate());
        }
        return film;
    }
}