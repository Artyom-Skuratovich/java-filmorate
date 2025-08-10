package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

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
public class UserDbStorageTest {
    @Autowired
    private UserDbStorage storage;

    @AfterEach
    public void clearDatabase() {
        List<User> users = storage.findAll();
        users.forEach(u -> storage.delete(u.getId()));
    }

    @Test
    public void shouldCreateOneUser() {
        User user = new User();
        user.setName("Mike");
        user.setLogin("M1kie");
        user.setEmail("mike@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        user = storage.create(user);
        Optional<User> fromDb = storage.find(user.getId());

        assertTrue(fromDb.isPresent());
        assertEquals(user.getLogin(), fromDb.get().getLogin());
    }

    @Test
    public void shouldFindUnconfirmedFriendRequests() {
        User user = new User();
        user.setName("Artyom");
        user.setLogin("Archie");
        user.setEmail("art@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        User friend = new User();
        friend.setName("Nikita");
        friend.setLogin("Nik");
        friend.setEmail("NIKE@mail.ru");
        friend.setBirthday(LocalDate.now().minusYears(15));

        user = storage.create(user);
        friend = storage.create(friend);
        storage.addFriend(user.getId(), friend.getId());

        List<User> unconfirmedRequests = storage.findUnconfirmedFriendRequests(friend.getId());

        assertEquals(1, unconfirmedRequests.size());
        assertEquals(user.getId(), unconfirmedRequests.getFirst().getId());
    }
}