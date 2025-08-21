package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

// Контроллер ленты событий
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FeedController {

    private final EventService eventService;
    private final UserService userService;

    // GET /users/{id}/feed — лента событий пользователя
    @GetMapping("/{id}/feed")
    public List<EventDto> getFeed(@PathVariable("id") long userId) {
        log.info("GET-запрос ленты событий, userId={}", userId);
        userService.find((int) userId); // рус: валидируем существование пользователя (404 если нет)
        return eventService.getFeed(userId).stream()
                .map(EventDto::from)
                .collect(Collectors.toList());
    }
}