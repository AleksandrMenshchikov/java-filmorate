package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BaseControllerTest {
    enum RequestMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    @BeforeEach
    void setUp() {
        FilmorateApplication.run(new String[]{""});
    }

    @AfterEach
    void tearDown() {
        FilmorateApplication.close();
    }

    final HttpResponse<String> getResponse(String path, String requestMethod, String jsonData) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        URI uri = URI.create("http://localhost:8080" + path);

        if (requestMethod.equals(RequestMethod.GET.toString())) {
            builder.GET();
        } else if (requestMethod.equals(RequestMethod.POST.toString())) {
            builder.POST(HttpRequest.BodyPublishers.ofString(jsonData));
        } else if (requestMethod.equals(RequestMethod.PUT.toString())) {
            builder.PUT(HttpRequest.BodyPublishers.ofString(jsonData));
        } else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
            builder.DELETE();
        }

        HttpRequest httpRequest = builder.uri(uri).version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json;charset=utf-8").build();

        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
