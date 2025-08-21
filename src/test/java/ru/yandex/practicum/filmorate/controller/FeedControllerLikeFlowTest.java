package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = "spring.sql.init.mode=never")
@Sql(scripts = {"/schema.sql", "/data.sql"})
class FeedControllerLikeFlowTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private int userId;
    private int filmId;

    @BeforeEach
    void setUp() throws Exception {
        // rus: генерируем уникальный логин для каждого запуска теста
        String login = "feeduser_" + System.nanoTime();

        // rus: создаём JSON для пользователя через ObjectMapper
        com.fasterxml.jackson.databind.node.ObjectNode user = mapper.createObjectNode();
        user.put("email", "feed@test.com");
        user.put("login", login);
        user.put("name", "Feed User");
        user.put("birthday", "1990-01-01");

        String userJson = mapper.writeValueAsString(user);

        // rus: создаём пользователя через REST и получаем его id
        String userRes = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode userNode = mapper.readTree(userRes);
        userId = userNode.get("id").asInt();

        // rus: создаём JSON для фильма через ObjectMapper
        com.fasterxml.jackson.databind.node.ObjectNode film = mapper.createObjectNode();
        film.put("name", "Film A");
        film.put("description", "desc");
        film.put("releaseDate", "2000-01-01");
        film.put("duration", 100);

        // rus: добавляем вложенный объект mpa (id = 1 из data.sql)
        com.fasterxml.jackson.databind.node.ObjectNode mpa = mapper.createObjectNode();
        mpa.put("id", 1);
        film.set("mpa", mpa);

        String filmJson = mapper.writeValueAsString(film);

        // rus: создаём фильм через REST и получаем его id
        String filmRes = mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode filmNode = mapper.readTree(filmRes);
        filmId = filmNode.get("id").asInt();
    }

    @Test
    void feed_shouldContainLikeAddAndRemove_inAscendingOrder() throws Exception {
        // rus: ставим лайк -> генерируется событие LIKE/ADD
        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        // rus: небольшая пауза, чтобы timestamp событий точно различался
        try {
            Thread.sleep(5);
        } catch (InterruptedException ignored) {
        }

        // rus: снимаем лайк -> генерируется событие LIKE/REMOVE
        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        // rus: читаем ленту и проверяем порядок и поля
        mockMvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                // 1) первое событие — поставили лайк
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].eventType").value("LIKE"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(filmId))
                .andExpect(jsonPath("$[0].timestamp", greaterThan(0L)))
                // 2) второе событие — сняли лайк
                .andExpect(jsonPath("$[1].eventType").value("LIKE"))
                .andExpect(jsonPath("$[1].operation").value("REMOVE"))
                .andExpect(jsonPath("$[1].entityId").value(filmId))
                .andExpect(jsonPath("$[1].timestamp", greaterThan(0L)));
    }
}
