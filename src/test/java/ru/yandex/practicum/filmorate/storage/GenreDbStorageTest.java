package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.database.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext
@Sql(scripts = "/data.sql", executionPhase = AFTER_TEST_METHOD)
public class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage genreStorage;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private MpaDbStorage mpaStorage;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        List<Film> films = filmStorage.findAll();
        films.forEach(f -> filmStorage.delete(f.getId()));
    }

    @Test
    public void shouldCreateAndDeleteGenre() {
        Genre genre = new Genre();
        genre.setName("New genre");

        genre = genreStorage.create(genre);

        assertTrue(genreStorage.find(genre.getId()).isPresent());

        genreStorage.delete(genre.getId());

        assertTrue(genreStorage.find(genre.getId()).isEmpty());
    }

    @Test
    public void shouldAddGenreToFilm() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("R")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        Film film = new Film();
        film.setName("Побег из Шоушенка");
        film.setDescription("Страх - это кандалы. Надежда - это свобода");
        film.setDuration(144);
        film.setReleaseDate(LocalDate.of(1994, 9, 10));
        film.setMpaId(mpa.getId());

        film = filmStorage.create(film);

        Genre genre = new Genre();
        genre.setName("New genre");

        genre = genreStorage.create(genre);

        genreStorage.addGenreToFilm(film.getId(), genre.getId());

        List<Genre> filmGenres = genreStorage.findGenresForFilm(film.getId());

        assertEquals(1, filmGenres.size());
        assertEquals(genre.getId(), filmGenres.getFirst().getId());

        genreStorage.delete(genre.getId());
    }
}