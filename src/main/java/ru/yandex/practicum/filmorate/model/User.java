package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public Integer getIdAsInteger() {
        return id;
    }

    public String getEmailSafe() {
        return email;
    }

    public String getLoginSafe() {
        return login;
    }

    public String getNameSafe() {
        return (name == null || name.isBlank()) ? login : name;
    }

    public LocalDate getBirthdaySafe() {
        return birthday;
    }
}