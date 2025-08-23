package ru.yandex.practicum.filmorate.util.converter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.storage.FilmSearchOption;

public class StringToFilmSearchOptionConverter implements Converter<String, FilmSearchOption> {
    @Override
    public FilmSearchOption convert(String source) {
        source = source.toUpperCase();
        if (source.equals("DIRECTOR,TITLE") || source.equals("TITLE,DIRECTOR")) {
            return FilmSearchOption.BOTH;
        }
        return FilmSearchOption.valueOf(source);
    }
}