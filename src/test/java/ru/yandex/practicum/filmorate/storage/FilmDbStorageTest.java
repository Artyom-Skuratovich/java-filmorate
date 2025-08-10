package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
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
public class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private MpaDbStorage mpaStorage;

    @AfterEach
    public void clearDatabase() {
        List<Film> films = filmStorage.findAll();
        films.forEach(f -> filmStorage.delete(f.getId()));
    }

    @Test
    public void shouldCreateOneFilm() {
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
        Optional<Film> fromDb = filmStorage.find(film.getId());

        assertTrue(fromDb.isPresent());
        assertEquals(film.getName(), fromDb.get().getName());
    }
}