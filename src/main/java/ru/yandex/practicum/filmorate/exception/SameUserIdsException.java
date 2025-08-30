package ru.yandex.practicum.filmorate.exception;

public class SameUserIdsException extends RuntimeException {
    public SameUserIdsException(String message) {
        super(message);
    }
}