package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.StorageUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage storage;

    public List<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa find(int id) {
        return StorageUtils.findModel(storage, id, String.format(
                "MPA с id=%d не найден",
                id
        ));
    }
}