package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Optional;

public final class StorageUtils {
    private StorageUtils() {
    }

    public static <T> T findModel(Storage<T> storage, int id, String errorMessage) {
        Optional<T> optionalModel = storage.find(id);
        if (optionalModel.isEmpty()) {
            throw new NotFoundException(errorMessage);
        }
        return optionalModel.get();
    }
}