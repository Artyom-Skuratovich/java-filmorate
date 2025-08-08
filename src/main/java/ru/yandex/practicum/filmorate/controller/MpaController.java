package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService service;

    @GetMapping
    public List<Mpa> findAll() {
        log.info("GET-запрос на получение списка всех MPA-рейтингов");
        return service.findAll();
    }

    @GetMapping("/{mpaId}")
    public Mpa find(@PathVariable int mpaId) {
        log.info("GET-запрос на получение MPA-рейтинга, id={}", mpaId);
        return service.find(mpaId);
    }
}