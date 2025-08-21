package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.CreateUserRequest;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

@SpringBootTest
@AutoConfigureMockMvc
// рус: поднимем схему/данные для интеграционного теста
@Sql(scripts = {"/schema.sql", "/data.sql"})
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    private long userId;

    @BeforeEach
    void setUp() {
        // рус: создаём пользователя через сервис, чтобы валидации прошли
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("feed@test.com");
        // rus: уникальный логин для каждого запуска теста
        req.setLogin("feeduser_" + System.nanoTime());
        req.setName("Feed User");
        req.setBirthday(LocalDate.of(1990, 1, 1));

        User u = userService.create(req);
        userId = u.getId();

        // рус: добавим 2 события через EventService (он сам проставит timestamp)
        eventService.addEvent(userId, EventType.LIKE,   Operation.ADD,    111L);
        // небольшая пауза, чтобы гарантированно отличались ts
        try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        eventService.addEvent(userId, EventType.FRIEND, Operation.REMOVE, 222L);
    }

    @Test
    void getFeed_shouldReturnEventsAscendingByTimestamp() throws Exception {
        // rus: вызываем GET /users/{id}/feed и проверяем структуру и порядок
        mockMvc.perform(get("/users/{id}/feed", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                // первый элемент — тот, что добавили первым (LIKE/ADD, entityId=111)
                .andExpect(jsonPath("$[0].userId").value((int) userId))
                .andExpect(jsonPath("$[0].eventType").value("LIKE"))
                .andExpect(jsonPath("$[0].operation").value("ADD"))
                .andExpect(jsonPath("$[0].entityId").value(111))
                .andExpect(jsonPath("$[0].timestamp", numberGreaterThan(0)))
                // второй элемент — FRIEND/REMOVE, entityId=222
                .andExpect(jsonPath("$[1].eventType").value("FRIEND"))
                .andExpect(jsonPath("$[1].operation").value("REMOVE"))
                .andExpect(jsonPath("$[1].entityId").value(222))
                .andExpect(jsonPath("$[1].timestamp", numberGreaterThan(0)));
    }

    // ---- Helper matcher: принимает Integer/Long/BigDecimal и сравнивает по longValue() ----
    private static Matcher<Object> numberGreaterThan(long min) {
        return new TypeSafeDiagnosingMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("a number > ").appendValue(min);
            }

            @Override
            protected boolean matchesSafely(Object item, Description mismatchDescription) {
                if (!(item instanceof Number n)) {
                    mismatchDescription.appendText("was ").appendValue(item);
                    return false;
                }
                long v = n.longValue();
                if (v <= min) {
                    mismatchDescription.appendText("number ").appendValue(v)
                            .appendText(" <= ").appendValue(min);
                    return false;
                }
                return true;
            }
        };
    }
}
