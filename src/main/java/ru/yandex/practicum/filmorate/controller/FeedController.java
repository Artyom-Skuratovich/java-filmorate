package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/users/{id}/feed")
    public List<EventDto> getFeed(@PathVariable("id") long userId) {
        // валидируем существование пользователя (404 если нет)
        userService.find((int) userId);
        // контроллер не преобразует данные — просто делегирует в сервис, который возвращает DTO
        log.info("GET /users/{}/feed", userId);
        return eventService.getFeed(userId);
    }
}