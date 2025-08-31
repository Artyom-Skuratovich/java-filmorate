package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.create.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.update.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public List<Director> findAll() {
        log.info("GET-запрос на получение списка всех режиссёров");
        return service.findAll();
    }

    @GetMapping("/{directorId}")
    public Director find(@PathVariable int directorId) {
        log.info("GET-запрос на получение режиссёра, id={}", directorId);
        return service.find(directorId);
    }

    @PostMapping
    public Director create(@Valid @RequestBody CreateDirectorRequest request) {
        log.info("POST-запрос на создание режиссёра");
        Director created = service.create(request);
        log.info("Режиссёр успешно создан, id={}", created.getId());
        return created;
    }

    @PutMapping
    public Director update(@Valid @RequestBody UpdateDirectorRequest request) {
        log.info("PUT-запрос на обновление режиссёра, id={}", request.getId());
        Director updated = service.update(request);
        log.info("Режиссёр с id={} успешно обновлён", updated.getId());
        return updated;
    }

    @DeleteMapping("/{directorId}")
    public void delete(@PathVariable int directorId) {
        log.info("DELETE-запрос на удаление режиссёра, id={}", directorId);
        service.delete(directorId);
    }
}