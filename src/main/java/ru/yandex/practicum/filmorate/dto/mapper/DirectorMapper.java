package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.create.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.update.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

public final class DirectorMapper {
    private DirectorMapper() {
    }

    public static Director mapToDirector(CreateDirectorRequest request) {
        Director director = new Director();
        director.setName(request.getName());
        return director;
    }

    public static Director updateDirectorProperties(Director director, UpdateDirectorRequest request) {
        director.setName(request.getName());
        return director;
    }
}