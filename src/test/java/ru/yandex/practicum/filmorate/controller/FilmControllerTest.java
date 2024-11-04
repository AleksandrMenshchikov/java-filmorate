package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.RequestMethod;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.util.Map;

class FilmControllerTest extends BaseControllerTest {
    private final String path = "/films";

    @Test
    void createFilm() throws IOException, InterruptedException {
        // Film create
        Map<String, ? extends Serializable> data = Map.of(
                "name", "nisi eiusmod",
                "description", "adipisicing",
                "releaseDate", "1967-03-25",
                "duration", 100
        );
        String s = new ObjectMapper().writeValueAsString(data);
        HttpResponse<String> response = getResponse(path, RequestMethod.POST.toString(), s);
        Assertions.assertEquals(200, response.statusCode());

        // Film create Fail name
        Map<String, ? extends Serializable> data1 = Map.of(
                "name", "",
                "description", "Description",
                "releaseDate", "1900-03-25",
                "duration", 200
        );
        String s1 = new ObjectMapper().writeValueAsString(data1);
        HttpResponse<String> response1 = getResponse(path, RequestMethod.POST.toString(), s1);
        Assertions.assertEquals(500, response1.statusCode());

        // Film create Fail description
        Map<String, ? extends Serializable> data2 = Map.of(
                "name", "Film name",
                "description", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                "releaseDate", "1900-03-25",
                "duration", 200
        );
        String s2 = new ObjectMapper().writeValueAsString(data2);
        HttpResponse<String> response2 = getResponse(path, RequestMethod.POST.toString(), s2);
        Assertions.assertEquals(500, response2.statusCode());

        // Film create Fail releaseDate
        Map<String, ? extends Serializable> data3 = Map.of(
                "name", "Name",
                "description", "Description",
                "releaseDate", "1890-03-25",
                "duration", 200
        );
        String s3 = new ObjectMapper().writeValueAsString(data3);
        HttpResponse<String> response3 = getResponse(path, RequestMethod.POST.toString(), s3);
        Assertions.assertEquals(500, response3.statusCode());

        // Film create Fail duration
        Map<String, ? extends Serializable> data4 = Map.of(
                "name", "Name",
                "description", "Description",
                "releaseDate", "1980-03-25",
                "duration", -200
        );
        String s4 = new ObjectMapper().writeValueAsString(data4);
        HttpResponse<String> response4 = getResponse(path, RequestMethod.POST.toString(), s4);
        Assertions.assertEquals(500, response4.statusCode());
    }

    @Test
    void updateFilm() throws IOException, InterruptedException {
        Map<String, ? extends Serializable> data = Map.of();
        String s = new ObjectMapper().writeValueAsString(data);
        HttpResponse<String> response = getResponse(path, RequestMethod.PUT.toString(), s);
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void getAllFilms() throws IOException, InterruptedException {
        HttpResponse<String> response = getResponse(path, RequestMethod.GET.toString(), null);
        Assertions.assertEquals(200, response.statusCode());
    }
}