package ru.yandex.practicum.filmorate.storage.abstraction;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    T create(T model);

    List<T> findAll();

    Optional<T> find(int id);

    T update(T model);

    void delete(int id);
}