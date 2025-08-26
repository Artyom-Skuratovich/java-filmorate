package ru.yandex.practicum.filmorate.util.converter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.storage.FilmSortOption;

public class StringToFilmSortOptionConverter implements Converter<String, FilmSortOption> {
    @Override
    public FilmSortOption convert(String source) {
        return FilmSortOption.valueOf(source.toUpperCase());
    }
}

