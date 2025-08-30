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
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.database.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext
@Sql(scripts = "/data.sql", executionPhase = AFTER_TEST_METHOD)
public class ReviewDbStorageTest {
    @Autowired
    private ReviewDbStorage reviewStorage;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private MpaDbStorage mpaStorage;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        List<Film> films = filmStorage.findAll();
        films.forEach(f -> filmStorage.delete(f.getId()));

        List<User> users = userStorage.findAll();
        users.forEach(u -> userStorage.delete(u.getId()));

        List<Review> reviews = reviewStorage.findAll();
        reviews.forEach(r -> reviewStorage.delete(r.getReviewId()));
    }

    @Test
    public void shouldFindAllReviewsForFilm() {
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

        User firstUser = new User();
        firstUser.setName("Mike");
        firstUser.setLogin("M1kie");
        firstUser.setEmail("mike@mail.ru");
        firstUser.setBirthday(LocalDate.now().minusYears(20));

        User secondUser = new User();
        secondUser.setName("Nikkie");
        secondUser.setLogin("N");
        secondUser.setEmail("nik@mail.ru");
        secondUser.setBirthday(LocalDate.now().minusYears(10));

        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);

        Review firstReview = new Review();
        firstReview.setFilmId(film.getId());
        firstReview.setUseful(20);
        firstReview.setContent("Нормально");
        firstReview.setUserId(firstUser.getId());
        firstReview.setIsPositive(false);

        Review secondReview = new Review();
        secondReview.setFilmId(film.getId());
        secondReview.setUseful(90);
        secondReview.setContent("Отлично");
        secondReview.setUserId(secondUser.getId());
        secondReview.setIsPositive(true);

        firstReview = reviewStorage.create(firstReview);
        secondReview = reviewStorage.create(secondReview);

        List<Review> reviews = reviewStorage.findFilmReview(film.getId(), 2);

        assertEquals(2, reviews.size());
        assertEquals(firstReview.getReviewId(), reviews.getFirst().getReviewId());
        assertEquals(secondReview.getReviewId(), reviews.get(1).getReviewId());
    }

    @Test
    public void shouldFindAllFilmsReview() {
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

        User firstUser = new User();
        firstUser.setName("Mike");
        firstUser.setLogin("M1kie");
        firstUser.setEmail("mike@mail.ru");
        firstUser.setBirthday(LocalDate.now().minusYears(20));

        User secondUser = new User();
        secondUser.setName("Nikkie");
        secondUser.setLogin("N");
        secondUser.setEmail("nik@mail.ru");
        secondUser.setBirthday(LocalDate.now().minusYears(10));

        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);

        Review firstReview = new Review();
        firstReview.setFilmId(film.getId());
        firstReview.setUseful(20);
        firstReview.setContent("Нормально");
        firstReview.setUserId(firstUser.getId());
        firstReview.setIsPositive(false);

        Review secondReview = new Review();
        secondReview.setFilmId(film.getId());
        secondReview.setUseful(90);
        secondReview.setContent("Отлично");
        secondReview.setUserId(secondUser.getId());
        secondReview.setIsPositive(true);

        firstReview = reviewStorage.create(firstReview);
        secondReview = reviewStorage.create(secondReview);

        List<Review> reviews = reviewStorage.findAllFilmsReview(2);

        assertEquals(2, reviews.size());
        assertEquals(firstReview.getReviewId(), reviews.getFirst().getReviewId());
        assertEquals(secondReview.getReviewId(), reviews.get(1).getReviewId());
    }

    @Test
    void createAndFindShouldWork() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("test", true, 100, 10));

        assertThat(created.getReviewId()).isGreaterThan(0);
        assertThat(created.getUseful()).isZero();

        Optional<Review> found = reviewStorage.find(created.getReviewId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("test");
        assertThat(found.get().getIsPositive()).isTrue();
        assertThat(found.get().getUserId()).isEqualTo(100);
        assertThat(found.get().getFilmId()).isEqualTo(10);
        assertThat(found.get().getUseful()).isZero();
    }

    @Test
    void findShouldReturnEmptyWhenIdMissing() {
        seedPrerequisites();
        assertThat(reviewStorage.find(999_999)).isEmpty();
    }

    @Test
    void updateShouldChangeContentAndPositiveKeepOthers() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("old", true, 1, 10));

        created.setContent("new");
        created.setIsPositive(false);

        int originalUser = created.getUserId();
        int originalFilm = created.getFilmId();
        int originalUseful = created.getUseful();

        reviewStorage.update(created);

        Review updated = reviewStorage.find(created.getReviewId()).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("new");
        assertThat(updated.getIsPositive()).isFalse();

        assertThat(updated.getUserId()).isEqualTo(originalUser);
        assertThat(updated.getFilmId()).isEqualTo(originalFilm);
        assertThat(updated.getUseful()).isEqualTo(originalUseful);
    }

    @Test
    void updateUsefulShouldUpdateOnlyUseful() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("text", true, 1, 10));
        created.setUseful(7);

        reviewStorage.updateUseful(created);

        Integer usefulFromDb = jdbcTemplate.queryForObject(
                "SELECT useful FROM reviews WHERE id = ?",
                Integer.class,
                created.getReviewId()
        );
        assertThat(usefulFromDb).isEqualTo(7);

        Review updated = reviewStorage.find(created.getReviewId()).orElseThrow();
        assertThat(updated.getUseful()).isEqualTo(7);
        assertThat(updated.getContent()).isEqualTo("text");
        assertThat(updated.getIsPositive()).isTrue();
        assertThat(updated.getUserId()).isEqualTo(1);
        assertThat(updated.getFilmId()).isEqualTo(10);
    }

    @Test
    void deleteShouldRemoveRow() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("toDelete", true, 1, 10));
        reviewStorage.delete(created.getReviewId());
        assertThat(reviewStorage.find(created.getReviewId())).isEmpty();
    }

    @Test
    void likeFlowShouldReturnTrueOnceThenFalse() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("like", true, 1, 10));
        reviewStorage.addLike(created.getReviewId(), 100);

        boolean first = reviewStorage.deleteLike(created.getReviewId(), 100);
        boolean second = reviewStorage.deleteLike(created.getReviewId(), 100);

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    void deleteLikeShouldReturnFalseWhenNotSet() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("nolike", true, 1, 10));
        boolean deleted = reviewStorage.deleteLike(created.getReviewId(), 777);
        assertFalse(deleted);
    }

    @Test
    void dislikeFlowShouldReturnTrueOnceThenFalse() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("dislike", true, 1, 10));
        reviewStorage.addDislike(created.getReviewId(), 200);

        boolean first = reviewStorage.deleteDislike(created.getReviewId(), 200);
        boolean second = reviewStorage.deleteDislike(created.getReviewId(), 200);

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    void deleteDislikeShouldReturnFalseWhenNotSet() {
        seedPrerequisites();
        Review created = reviewStorage.create(makeReview("nodislike", true, 1, 10));
        boolean deleted = reviewStorage.deleteDislike(created.getReviewId(), 555);
        assertFalse(deleted);
    }

    @Test
    void findAllFilmsReviewShouldRespectLimit() {
        seedPrerequisites();
        reviewStorage.create(makeReview("r1", true, 1, 10));
        reviewStorage.create(makeReview("r2", false, 1, 11));
        reviewStorage.create(makeReview("r3", true, 1, 12));

        List<Review> limited = reviewStorage.findAllFilmsReview(2);
        assertThat(limited).hasSize(2);

        List<Review> all = reviewStorage.findAllFilmsReview(10);
        assertThat(all).hasSize(3);

        List<Review> none = reviewStorage.findAllFilmsReview(0);
        assertThat(none).isEmpty();
    }

    @Test
    void findFilmReviewShouldFilterAndRespectLimit() {
        seedPrerequisites();
        reviewStorage.create(makeReview("a", true, 1, 10));
        reviewStorage.create(makeReview("b", true, 1, 10));
        reviewStorage.create(makeReview("c", false, 1, 11));

        List<Review> onlyFilm10 = reviewStorage.findFilmReview(10, 5);
        assertThat(onlyFilm10).hasSize(2).allMatch(r -> r.getFilmId() == 10);

        List<Review> limited = reviewStorage.findFilmReview(10, 1);
        assertThat(limited).hasSize(1);

        List<Review> none = reviewStorage.findFilmReview(10, 0);
        assertThat(none).isEmpty();
    }

    private void seedPrerequisites() {
        Optional<Mpa> optionalMpa = mpaStorage.findAll().stream().filter(m -> m.getName().equals("G")).findFirst();
        assertTrue(optionalMpa.isPresent());
        Mpa mpa = optionalMpa.get();

        jdbcTemplate.update("""
                INSERT INTO users (id, email, login, name, birthday) VALUES
                (1, 'u1@mail', 'u1', 'U1', '1990-01-01'),
                (2, 'u2@mail', 'u2', 'U2', '1990-02-02'),
                (100, 'u100@mail', 'u100', 'U100', '1991-01-01'),
                (200, 'u200@mail', 'u200', 'U200', '1992-02-02')
                """);
        jdbcTemplate.update(String.format("""
                INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES
                (10, 'F10', 'd', '2020-01-01', 100, %d),
                (11, 'F11', 'd', '2020-01-02', 100, %d),
                (12, 'F12', 'd', '2020-01-03', 100, %d),
                (20, 'F20', 'd', '2020-02-01', 100, %d),
                (42, 'F42', 'd', '2020-03-01', 100, %d)
                """, mpa.getId(), mpa.getId(), mpa.getId(), mpa.getId(), mpa.getId()));
    }

    private static Review makeReview(String content, boolean positive, int userId, int filmId) {
        Review r = new Review();
        r.setContent(content);
        r.setIsPositive(positive);
        r.setUserId(userId);
        r.setFilmId(filmId);
        r.setUseful(0);
        return r;
    }
}