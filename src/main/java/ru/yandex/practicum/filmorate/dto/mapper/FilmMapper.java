package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.create.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.update.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public final class FilmMapper {
    private FilmMapper() {
    }

    public static FilmDto mapToFilmDto(Film film, Mpa mpa, List<Genre> genres, List<Director> directors) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setDuration(film.getDuration());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setMpa(mpa);
        dto.setGenres(genres);
        dto.setDirectors(directors);

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
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpaId(request.getMpa().getId());
        return film;
    }
}