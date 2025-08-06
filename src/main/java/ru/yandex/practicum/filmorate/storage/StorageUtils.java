package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.abstraction.Storage;

import java.util.Optional;

public final class StorageUtils {
    private StorageUtils() {
    }

    public static <T> T findModelOrThrowIfDoesNotExist(Storage<T> storage, int id, String errorMessage) {
        Optional<T> optionalModel = storage.find(id);
        if (optionalModel.isEmpty()) {
            throw new NotFoundException(errorMessage);
        }
        return optionalModel.get();
    }
}