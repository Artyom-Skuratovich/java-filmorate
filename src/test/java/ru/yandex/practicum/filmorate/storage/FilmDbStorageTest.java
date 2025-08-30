package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private DirectorDbStorage directorStorage;
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        List<Film> films = filmStorage.findAll();
        films.forEach(f -> filmStorage.delete(f.getId()));

        List<User> users = userStorage.findAll();
        users.forEach(u -> userStorage.delete(u.getId()));

        List<Director> directors = directorStorage.findAll();
        directors.forEach(d -> directorStorage.delete(d.getId()));
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

    @Test
    public void shouldReturnOneMostPopularFilm() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("R")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        Film film = new Film();
        film.setName("Побег из Шоушенка");
        film.setDescription("Страх - это кандалы. Надежда - это свобода");
        film.setDuration(144);
        film.setReleaseDate(LocalDate.of(1994, 9, 10));
        film.setMpaId(mpa.getId());

        User user = new User();
        user.setName("Mike");
        user.setLogin("M1kie");
        user.setEmail("mike@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        film = filmStorage.create(film);
        user = userStorage.create(user);
        filmStorage.addLike(film.getId(), user.getId());

        List<Film> mostPopular = filmStorage.findMostPopularFilms(1000, null, null);

        assertEquals(1, mostPopular.size());
    }

    @Test
    public void shouldUpdateFilm() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("R")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        Film film = new Film();
        film.setName("Побег из Шоушенка");
        film.setDescription("Страх - это кандалы. Надежда - это свобода");
        film.setDuration(144);
        film.setReleaseDate(LocalDate.of(1994, 9, 10));
        film.setMpaId(mpa.getId());

        Film created = filmStorage.create(film);

        created.setName("Искупление Шоушенком");
        Film updated = filmStorage.update(created);

        assertEquals("Искупление Шоушенком", updated.getName());
    }

    @Test
    public void shouldFindFilmByTitle() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("R")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        Film first = new Film();
        first.setName("Побег из Шоушенка");
        first.setDescription("Страх - это кандалы. Надежда - это свобода");
        first.setDuration(144);
        first.setReleaseDate(LocalDate.of(1994, 9, 10));
        first.setMpaId(mpa.getId());

        filmStorage.create(first);

        optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("PG-13")).findFirst();
        assertTrue(optionalMpa.isPresent());
        mpa = optionalMpa.get();

        Film second = new Film();
        second.setName("Интерстеллар");
        second.setDescription("Следующий шаг человечества станет величайшим");
        second.setDuration(169);
        second.setReleaseDate(LocalDate.of(2015, 4, 9));
        second.setMpaId(mpa.getId());

        second = filmStorage.create(second);

        List<Film> films = filmStorage.search("ЕлЛ", FilmSearchOption.TITLE);

        assertEquals(1, films.size());
        assertEquals(second.getId(), films.getFirst().getId());
    }

    @Test
    public void shouldFindFilmsForDirectorSortedByYear() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("R")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        Film first = new Film();
        first.setName("Побег из Шоушенка");
        first.setDescription("Страх - это кандалы. Надежда - это свобода");
        first.setDuration(144);
        first.setReleaseDate(LocalDate.of(1994, 9, 10));
        first.setMpaId(mpa.getId());

        filmStorage.create(first);

        optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("PG-13")).findFirst();
        assertTrue(optionalMpa.isPresent());
        mpa = optionalMpa.get();

        Film second = new Film();
        second.setName("Интерстеллар");
        second.setDescription("Следующий шаг человечества станет величайшим");
        second.setDuration(169);
        second.setReleaseDate(LocalDate.of(2015, 4, 9));
        second.setMpaId(mpa.getId());

        second = filmStorage.create(second);

        Film third = new Film();
        third.setName("Optional");
        third.setDescription("Optional");
        third.setDuration(60);
        third.setReleaseDate(LocalDate.now());
        third.setMpaId(mpa.getId());

        third = filmStorage.create(third);

        Director director = new Director();
        director.setName("Кристофер Нолан");

        director = directorStorage.create(director);

        directorStorage.addDirectorToFilm(second.getId(), director.getId());
        directorStorage.addDirectorToFilm(third.getId(), director.getId());

        List<Film> films = filmStorage.findDirectorFilmsSorted(director.getId(), FilmSortOption.YEAR);

        assertEquals(2, films.size());
        assertEquals(second.getId(), films.getFirst().getId());
        assertEquals(third.getId(), films.get(1).getId());
    }

    @Test
    public void recommendationsHappyPathSingleBestUser() {
        seedPrerequisites();

        like(1, 1);
        like(1, 2);

        like(2, 1);
        like(2, 2);
        like(2, 3);

        like(3, 2);
        like(3, 4);

        List<Film> recs = filmStorage.findFilmRecommendations(1);

        assertThat(recs).extracting(Film::getId).containsExactly(3);
    }

    @Test
    void recommendationsAllCandidatesAlreadyLikedByTargetEmpty() {
        seedPrerequisites();

        like(1, 1);
        like(1, 2);
        like(2, 1);
        like(2, 2);

        List<Film> recs = filmStorage.findFilmRecommendations(1);
        assertThat(recs).isEmpty();
    }

    @Test
    void recommendationsTargetHasNoLikesEmpty() {
        seedPrerequisites();

        like(2, 3);
        like(3, 4);

        List<Film> recs = filmStorage.findFilmRecommendations(1);
        assertThat(recs).isEmpty();
    }

    private void like(int userId, int filmId) {
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (?,?)", userId, filmId);
    }

    private void seedPrerequisites() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("G")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        jdbc.update("""
                    INSERT INTO users(id, email, login, name, birthday) VALUES
                    (1, 'u1@mail', 'u1', 'U1', '1990-01-01'),
                    (2, 'u2@mail', 'u2', 'U2', '1990-02-02'),
                    (3, 'u3@mail', 'u3', 'U3', '1990-03-03'),
                    (4, 'u4@mail', 'u4', 'U4', '1990-04-04')
                """);
        // Films: F1..F5
        jdbc.update(String.format("""
                    INSERT INTO films(id, name, description, release_date, duration, mpa_id) VALUES
                    (1,'F1','d','2020-01-01',100,%d),
                    (2,'F2','d','2020-01-02',100,%d),
                    (3,'F3','d','2020-01-03',100,%d),
                    (4,'F4','d','2020-01-04',100,%d),
                    (5,'F5','d','2020-01-05',100,%d)
                """, mpa.getId(), mpa.getId(), mpa.getId(), mpa.getId(), mpa.getId()));
    }
}