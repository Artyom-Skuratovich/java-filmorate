package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.StorageUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage storage;

    public List<Genre> findAll() {
        return storage.findAll();
    }

    public Genre find(int id) {
        return StorageUtils.findModel(storage, id, String.format(
                "Жанр с id=%d не найден",
                id
        ));
    }
}