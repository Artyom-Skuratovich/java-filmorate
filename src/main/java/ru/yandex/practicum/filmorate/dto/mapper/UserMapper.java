package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static User mapToUser(CreateUserRequest request) {
        User user = new User();
        user.setBirthday(request.getBirthday());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        return user;
    }

    public static User updateUserProperties(User user, UpdateUserRequest request) {
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getLogin() != null) {
            user.setLogin(request.getLogin());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        return user;
    }
}