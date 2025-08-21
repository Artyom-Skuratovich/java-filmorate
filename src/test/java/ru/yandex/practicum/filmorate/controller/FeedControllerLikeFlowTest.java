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
        // rus: создаём пользователя через REST, т.к. DefaultEventService валидирует существование пользователя
        String login = "feeduser_" + System.nanoTime();

        String userJson = """
                {
                  "email": "feed@test.com",
                  "login": "%s",
                  "name": "Feed User",
                  "birthday": "1990-01-01"
                }
                """.formatted(login);
        // rus: уникальный логин для каждого запуска теста

        String userRes = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode userNode = mapper.readTree(userRes);
        userId = userNode.get("id").asInt();

        // rus: создаём фильм через REST (минимально валидный), mpa.id = 1 (есть в data.sql)
        String filmJson = """
                {
                  "name": "Film A",
                  "description": "desc",
                  "releaseDate": "2000-01-01",
                  "duration": 100,
                  "mpa": { "id": 1 }
                }
                """;

        String filmRes = mockMvc.perform(post("/films")
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
