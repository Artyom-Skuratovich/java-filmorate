package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmSearchOption;
import ru.yandex.practicum.filmorate.storage.FilmSortOption;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;
    private static final String CREATE_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_COMMON_FILMS_QUERY = """
            SELECT f.*
            FROM films f
            JOIN likes l1 ON f.id = l1.film_id AND l1.user_id = ?
            JOIN likes l2 ON f.id = l2.film_id AND l2.user_id = ?
            ORDER BY (
                SELECT COUNT(*)
                FROM likes l
                WHERE l.film_id = f.id
            ) DESC
            """;
    private static final String FIND_FILM_RECOMMENDATIONS_QUERY = """
            WITH user_likes AS (
              SELECT film_id
              FROM likes
              WHERE user_id = ?
            ),
            overlaps AS (
              SELECT l2.user_id AS other_user, COUNT(*) AS common_cnt
              FROM likes l1
              JOIN likes l2 ON l1.film_id = l2.film_id
              WHERE l1.user_id = ? AND l2.user_id <> ?
              GROUP BY l2.user_id
            ),
            best AS (
              SELECT other_user
              FROM overlaps
              WHERE common_cnt = (SELECT COALESCE(MAX(common_cnt), 0) FROM overlaps)
            ),
            candidates AS (
              SELECT l.film_id
              FROM likes l
              JOIN best b ON b.other_user = l.user_id
            )
            SELECT f.*, COUNT(*) AS votes
            FROM films f
            JOIN candidates c ON c.film_id = f.id
            LEFT JOIN user_likes ul ON ul.film_id = f.id
            WHERE ul.film_id IS NULL
            GROUP BY f.id
            ORDER BY votes DESC, f.id ASC;
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findMostPopularFilms(int count, Integer genreId, Integer year) {
        StringBuilder query = new StringBuilder("""
                SELECT f.*
                FROM films f
                """);
        List<Object> params = new ArrayList<>(3);
        if (genreId != null) {
            query.append("JOIN films_genres fg ON f.id = fg.film_id ");
        }
        query.append("""
                LEFT JOIN likes l ON f.id = l.film_id
                WHERE 1=1
                """);
        if (genreId != null) {
            query.append("AND fg.genre_id = ? ");
            params.add(genreId);
        }
        if (year != null) {
            query.append("AND EXTRACT(YEAR FROM f.release_date) = ? ");
            params.add(year);
        }
        query.append("""
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
                """);
        params.add(count);
        return findMany(query.toString(), params.toArray());
    }

    public boolean addLike(int filmId, int userId) {
        try {
            return jdbc.update(ADD_LIKE_QUERY, filmId, userId) == 1;
        } catch (org.springframework.dao.DuplicateKeyException ignore) {
            return false;
        }
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        return jdbc.update(DELETE_LIKE_QUERY, filmId, userId) != 0;
    }

    @Override
    public Film create(Film model) {
        int id = create(
                CREATE_QUERY,
                model.getName(),
                model.getDescription(),
                model.getReleaseDate(),
                model.getDuration(),
                model.getMpaId()
        );
        model.setId(id);
        return model;
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public Film update(Film model) {
        update(
                UPDATE_QUERY,
                model.getName(),
                model.getDescription(),
                model.getReleaseDate(),
                model.getDuration(),
                model.getMpaId(),
                model.getId()
        );
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> findFilmRecommendations(int userId) {
        return findMany(FIND_FILM_RECOMMENDATIONS_QUERY, userId, userId, userId);
    }

    @Override
    public List<Film> findDirectorFilmsSorted(int directorId, FilmSortOption sortOption) {
        StringBuilder query = new StringBuilder("""
                SELECT f.*
                FROM films f
                JOIN films_directors fd ON f.id = fd.film_id
                """);

        String orderBy = "EXTRACT(YEAR FROM f.release_date)";

        if (sortOption == FilmSortOption.LIKES) {
            query.append("""
                    LEFT JOIN likes l ON fd.film_id = l.film_id
                    """);
            orderBy = "COUNT(l.film_id) DESC";
        }

        query.append(String.format("""
                WHERE fd.director_id = ?
                GROUP BY f.id
                ORDER BY %s
                """, orderBy)
        );

        return findMany(query.toString(), directorId);
    }

    @Override
    public List<Film> search(String pattern, FilmSearchOption opt) {
        // подготавливаем шаблон
        pattern = "%" + pattern + "%";
        List<Object> params = new ArrayList<>(2);

        StringBuilder sql = new StringBuilder("""
        SELECT f.*
        FROM (
            SELECT f.id,
                   COALESCE(lc.likes_cnt, 0) AS likes_cnt
            FROM films f
            LEFT JOIN (
                SELECT film_id, COUNT(*) AS likes_cnt
                FROM likes
                GROUP BY film_id
            ) lc ON lc.film_id = f.id
        """);

        // опционально присоединяем режиссёров
        boolean needDirector = (opt == FilmSearchOption.DIRECTOR) || (opt == FilmSearchOption.BOTH);
        if (needDirector) {
            sql.append("""
            LEFT JOIN films_directors fd ON fd.film_id = f.id
            LEFT JOIN directors d ON d.id = fd.id
        """.replace("d.id = fd.id","d.id = fd.director_id"));
        }

        // WHERE с ИЛИ по выбранным полям
        StringBuilder where = new StringBuilder("WHERE (");
        String sep = "";
        if (needDirector) {
            where.append("(d.name IS NOT NULL AND LOWER(d.name) LIKE LOWER(?))");
            params.add(pattern);
            sep = " OR ";
        }
        boolean needTitle = (opt == FilmSearchOption.TITLE) || (opt == FilmSearchOption.BOTH);
        if (needTitle) {
            where.append(sep).append("LOWER(f.name) LIKE LOWER(?)");
            params.add(pattern);
        }
        where.append(")\n");

        sql.append(where)
                .append("GROUP BY f.id, lc.likes_cnt\n")        // группируем только по id и количеству лайков
                .append(") m\n")                                // закрыли подзапрос matched
                .append("JOIN films f ON f.id = m.id\n")
                .append("ORDER BY m.likes_cnt DESC, f.id ASC"); // сортировка по популярности

        return findMany(sql.toString(), params.toArray());
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        return findMany(FIND_COMMON_FILMS_QUERY, userId, friendId);
    }
}