package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.RequestMethod;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

class UserControllerTest extends BaseControllerTest {
    private final String path = "/users";

    @Test
    void createUser() throws IOException, InterruptedException {
        // User create
        Map<String, ? extends Serializable> data = Map.of(
                "login", "dolore",
                "name", "Nick Name",
                "email", "mail@mail.ru",
                "birthday", "1946-08-20"
        );
        String s = new ObjectMapper().writeValueAsString(data);
        HttpResponse<String> response = getResponse(path, RequestMethod.POST.toString(), s);
        Assertions.assertEquals(200, response.statusCode());

        // Create user with empty name
        Map<String, ? extends Serializable> data1 = Map.of(
                "login", "dolore",
                "email", "mail@mail.ru",
                "birthday", "1946-08-20"
        );
        String s1 = new ObjectMapper().writeValueAsString(data1);
        HttpResponse<String> response1 = getResponse(path, RequestMethod.POST.toString(), s1);
        Assertions.assertEquals(200, response1.statusCode());

        // User create Fail login
        Map<String, ? extends Serializable> data2 = Map.of(
                "email", "mail@mail.ru",
                "birthday", "1946-08-20"
        );
        String s2 = new ObjectMapper().writeValueAsString(data2);
        HttpResponse<String> response2 = getResponse(path, RequestMethod.POST.toString(), s2);
        Assertions.assertEquals(500, response2.statusCode());

        // User create Fail email
        Map<String, ? extends Serializable> data3 = Map.of(
                "login", "dolore",
                "name", "",
                "email", "mail.ru",
                "birthday", "1946-08-20"
        );
        String s3 = new ObjectMapper().writeValueAsString(data3);
        HttpResponse<String> response3 = getResponse(path, RequestMethod.POST.toString(), s3);
        Assertions.assertEquals(500, response3.statusCode());

        // User create Fail birthday
        Map<String, ? extends Serializable> data4 = Map.of(
                "login", "dolore",
                "name", "",
                "email", "test@mail.ru",
                "birthday", LocalDate.now().plusDays(1).toString()
        );
        String s4 = new ObjectMapper().writeValueAsString(data4);
        HttpResponse<String> response4 = getResponse(path, RequestMethod.POST.toString(), s4);
        Assertions.assertEquals(500, response4.statusCode());
    }

    @Test
    void updateUser() throws IOException, InterruptedException {
        Map<String, ? extends Serializable> data = Map.of();
        String s = new ObjectMapper().writeValueAsString(data);
        HttpResponse<String> response = getResponse(path, RequestMethod.PUT.toString(), s);
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void getAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = getResponse(path, RequestMethod.GET.toString(), null);
        Assertions.assertEquals(200, response.statusCode());
    }
}