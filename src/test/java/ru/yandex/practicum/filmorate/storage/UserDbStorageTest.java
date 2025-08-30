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
    private UserDbStorage userStorage;

    @AfterEach
    public void clearDatabase() {
        List<User> users = userStorage.findAll();
        users.forEach(u -> userStorage.delete(u.getId()));
    }

    @Test
    public void shouldCreateOneUser() {
        User user = new User();
        user.setName("Mike");
        user.setLogin("M1kie");
        user.setEmail("mike@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        user = userStorage.create(user);
        Optional<User> fromDb = userStorage.find(user.getId());

        assertTrue(fromDb.isPresent());
        assertEquals(user.getLogin(), fromDb.get().getLogin());
    }

    @Test
    public void shouldUpdateUser() {
        User user = new User();
        user.setName("Mike");
        user.setLogin("M1kie");
        user.setEmail("mike@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        user = userStorage.create(user);

        user.setEmail("new_user_email@gmail.com");

        user = userStorage.update(user);

        assertEquals("new_user_email@gmail.com", user.getEmail());
    }

    @Test
    public void shouldDeleteUser() {
        User user = new User();
        user.setName("Mike");
        user.setLogin("M1kie");
        user.setEmail("mike@mail.ru");
        user.setBirthday(LocalDate.now().minusYears(20));

        user = userStorage.create(user);
        userStorage.delete(user.getId());

        assertTrue(userStorage.find(user.getId()).isEmpty());
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

        user = userStorage.create(user);
        friend = userStorage.create(friend);
        userStorage.addFriend(user.getId(), friend.getId());

        List<User> unconfirmedRequests = userStorage.findUnconfirmedFriendRequests(friend.getId());

        assertEquals(1, unconfirmedRequests.size());
        assertEquals(user.getId(), unconfirmedRequests.getFirst().getId());
    }

    @Test
    public void shouldFindFriendsForUser() {
        User target = new User();
        target.setName("Artyom");
        target.setLogin("Archie");
        target.setEmail("art@mail.ru");
        target.setBirthday(LocalDate.now().minusYears(20));

        User firstFriend = new User();
        firstFriend.setName("Nikita");
        firstFriend.setLogin("Nik");
        firstFriend.setEmail("NIKE@mail.ru");
        firstFriend.setBirthday(LocalDate.now().minusYears(15));

        User secondFriend = new User();
        secondFriend.setName("Al Halil");
        secondFriend.setLogin("Alk");
        secondFriend.setEmail("alk@gmail.ru");
        secondFriend.setBirthday(LocalDate.now().minusYears(30));

        User thirdFriend = new User();
        thirdFriend.setName("Pedro");
        thirdFriend.setLogin("pedro");
        thirdFriend.setEmail("pedr0@gmail.ru");
        thirdFriend.setBirthday(LocalDate.now().minusYears(29));

        target = userStorage.create(target);
        firstFriend = userStorage.create(firstFriend);
        secondFriend = userStorage.create(secondFriend);
        thirdFriend = userStorage.create(thirdFriend);

        userStorage.addFriend(target.getId(), firstFriend.getId());
        userStorage.addFriend(target.getId(), secondFriend.getId());
        userStorage.addFriend(target.getId(), thirdFriend.getId());

        userStorage.deleteFriend(target.getId(), secondFriend.getId());

        List<User> friends = userStorage.findFriends(target.getId());

        assertEquals(2, friends.size());
        assertEquals(firstFriend.getId(), friends.getFirst().getId());
        assertEquals(thirdFriend.getId(), friends.getLast().getId());
    }

    @Test
    public void shouldFindCommonFriends() {
        User first = new User();
        first.setName("Artyom");
        first.setLogin("Archie");
        first.setEmail("art@mail.ru");
        first.setBirthday(LocalDate.now().minusYears(20));

        User second = new User();
        second.setName("Nikita");
        second.setLogin("Nik");
        second.setEmail("NIKE@mail.ru");
        second.setBirthday(LocalDate.now().minusYears(15));

        User third = new User();
        third.setName("Al Halil");
        third.setLogin("Alk");
        third.setEmail("alk@gmail.ru");
        third.setBirthday(LocalDate.now().minusYears(30));

        User fourth = new User();
        fourth.setName("Pedro");
        fourth.setLogin("pedro");
        fourth.setEmail("pedr0@gmail.ru");
        fourth.setBirthday(LocalDate.now().minusYears(29));

        first = userStorage.create(first);
        second = userStorage.create(second);
        third = userStorage.create(third);
        fourth = userStorage.create(fourth);

        userStorage.addFriend(first.getId(), third.getId());
        userStorage.addFriend(first.getId(), fourth.getId());

        userStorage.addFriend(second.getId(), first.getId());
        userStorage.addFriend(second.getId(), third.getId());
        userStorage.addFriend(second.getId(), fourth.getId());

        List<User> commonFriends = userStorage.findCommonFriends(first.getId(), second.getId());

        assertEquals(2, commonFriends.size());
        assertEquals(third.getId(), commonFriends.getFirst().getId());
        assertEquals(fourth.getId(), commonFriends.getLast().getId());
    }
}