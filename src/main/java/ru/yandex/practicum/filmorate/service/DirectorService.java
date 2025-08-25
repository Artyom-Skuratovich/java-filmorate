package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.create.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.dto.update.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.StorageUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public List<Director> findAll() {
        return storage.findAll();
    }

    public Director find(int directorId) {
        return StorageUtils.findModel(storage, directorId, String.format(
                "Режиссёр с id=%d не найден",
                directorId
        ));
    }

    public Director create(CreateDirectorRequest request) {
        return storage.create(DirectorMapper.mapToDirector(request));
    }

    public Director update(UpdateDirectorRequest request) {
        Director director = StorageUtils.findModel(storage, request.getId(), String.format(
                "Режиссёр с id=%d не найден",
                request.getId()
        ));
        return storage.update(DirectorMapper.updateDirectorProperties(director, request));
    }

    public void delete(int directorId) {
        storage.delete(directorId);
    }
}