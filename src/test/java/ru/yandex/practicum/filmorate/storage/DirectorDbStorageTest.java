package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.database.DirectorDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext
@Sql(scripts = "/data.sql", executionPhase = AFTER_TEST_METHOD)
public class DirectorDbStorageTest {
    @Autowired
    private DirectorDbStorage directorStorage;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        List<Director> directors = directorStorage.findAll();
        directors.forEach(d -> directorStorage.delete(d.getId()));
    }

    @Test
    public void shouldCreateDirector() {
        Director director = new Director();
        director.setName("Кристофер Нолан");

        director = directorStorage.create(director);

        Optional<Director> optionalDirector = directorStorage.find(director.getId());

        assertTrue(optionalDirector.isPresent());
        assertEquals(director.getId(), optionalDirector.get().getId());
    }

    @Test
    public void shouldUpdateDirector() {
        Director director = new Director();
        director.setName("Кристофер Нолан");

        director = directorStorage.create(director);

        director.setName("New name");

        director = directorStorage.update(director);

        assertEquals("New name", director.getName());
    }

    @Test
    public void shouldDeleteDirector() {
        Director director = new Director();
        director.setName("Кристофер Нолан");

        director = directorStorage.create(director);

        directorStorage.delete(director.getId());

        assertTrue(directorStorage.find(director.getId()).isEmpty());
    }
}