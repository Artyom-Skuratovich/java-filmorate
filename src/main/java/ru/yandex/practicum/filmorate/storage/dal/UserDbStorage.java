package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_UNCONFIRMED_FRIEND_REQUESTS_QUERY = """
            SELECT *
                FROM users
                WHERE id IN (
                    SELECT f.user_id
                    FROM friends AS f
                    WHERE f.friend_id = ? AND NOT EXISTS (
                        SELECT 1
                        FROM friends AS f2
                        WHERE f2.user_id = ? AND f2.friend_id = f.user_id
                    )
                );
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT u.*
            FROM users AS u
            INNER JOIN friends ON u.id = friend_id
            WHERE user_id = ?
            INTERSECT
            SELECT u.*
            FROM users AS u
            INNER JOIN friends ON u.id = friend_id
            WHERE user_id = ?
            """;
    private static final String FIND_FRIENDS_QUERY = """
            SELECT u.*
            FROM users AS u
            INNER JOIN friends ON u.id = friend_id
            WHERE user_id = ?
            """;
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
    private static final String CREATE_QUERY = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findUnconfirmedFriendRequests(int userId) {
        return findMany(FIND_UNCONFIRMED_FRIEND_REQUESTS_QUERY, userId, userId);
    }

    @Override
    public List<User> findCommonFriends(int firstUserId, int secondUserId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, firstUserId, secondUserId);
    }

    @Override
    public List<User> findFriends(int userId) {
        return findMany(FIND_FRIENDS_QUERY, userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public User create(User model) {
        int id = create(
                CREATE_QUERY,
                model.getEmail(),
                model.getLogin(),
                model.getName(),
                model.getBirthday()
        );
        model.setId(id);
        return model;
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> find(int id) {
        return findOne(FIND_QUERY, id);
    }

    @Override
    public User update(User model) {
        update(
                UPDATE_QUERY,
                model.getEmail(),
                model.getLogin(),
                model.getName(),
                model.getBirthday(),
                model.getId()
        );
        return model;
    }

    @Override
    public void delete(int id) {
        delete(DELETE_QUERY, id);
    }
}