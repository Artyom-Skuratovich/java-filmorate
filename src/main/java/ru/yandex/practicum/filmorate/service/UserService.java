package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.abstraction.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public List<User> getAll() {
        return storage.getAll();
    }

    public User create(User user) {
        checkName(user);
        return storage.create(user);
    }

    public User update(User user) {
        checkName(user);
        return storage.create(user);
    }


    public User addFriend(int userId, int friendId) {
        Optional<User> optionalUser = storage.get(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format(
                    "Не удалось добавить друга пользователю с id=%d, так как такого пользователя не существует",
                    userId
            ));
        }

        Optional<User> optionalFriend = storage.get(friendId);
        if (optionalFriend.isEmpty()) {
            throw new NotFoundException(String.format(
                    "Не удалось добавить друга с id=%d, так как такого пользователя не существует",
                    friendId
            ));
        }

        User user = optionalUser.get();
        user.getFriends().add(friendId);
        return storage.update(user);
    }

    public User removeFriend(int userId, int friendId) {
        return null;
    }

    private static void checkName(User user) {
        if ((user.getName() == null) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}