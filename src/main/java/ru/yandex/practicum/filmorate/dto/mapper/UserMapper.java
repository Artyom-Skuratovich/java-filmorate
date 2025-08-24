package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.create.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.update.UpdateUserRequest;
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
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        return user;
    }
}