package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = FeedController.class)
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /users/{id}/feed — 200 и массив событий")
    void getFeed_shouldReturn200AndArray() throws Exception {
        long userId = 1L;

        // имитируем успешную валидацию пользователя
        Mockito.doReturn(null).when(userService).find((int) userId);

        // подготавливаем DTO событий
        EventDto e1 = new EventDto(101L, 1_000L, userId, EventType.LIKE, Operation.ADD, 10L);
        EventDto e2 = new EventDto(102L, 2_000L, userId, EventType.FRIEND, Operation.REMOVE, 20L);
        Mockito.when(eventService.getFeed(userId)).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/users/{id}/feed", userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is(101)))
                .andExpect(jsonPath("$[0].timestamp", is(1000)))
                .andExpect(jsonPath("$[0].userId", is((int) userId)))
                .andExpect(jsonPath("$[0].eventType", is("LIKE")))
                .andExpect(jsonPath("$[0].operation", is("ADD")))
                .andExpect(jsonPath("$[0].entityId", is(10)))
                .andExpect(jsonPath("$[1].eventId", is(102)))
                .andExpect(jsonPath("$[1].timestamp", is(2000)))
                .andExpect(jsonPath("$[1].userId", is((int) userId)))
                .andExpect(jsonPath("$[1].eventType", is("FRIEND")))
                .andExpect(jsonPath("$[1].operation", is("REMOVE")))
                .andExpect(jsonPath("$[1].entityId", is(20)));

        Mockito.verify(userService).find((int) userId);
        Mockito.verify(eventService).getFeed(userId);
        Mockito.verifyNoMoreInteractions(eventService, userService);
    }
}