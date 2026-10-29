package com.mohamed.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiErrorIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void missingTask_returns404_inNewShape() throws Exception {
        HttpResponse<String> response = get("/tasks/999999");

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body())
                .contains("\"status\":404")
                .contains("\"error\":\"Not Found\"")
                .contains("\"message\":\"Task 999999 not found\"")
                .contains("\"fieldErrors\":{}");
    }

    @Test
    void emptyTitle_returns400_withFieldError() throws Exception {
        HttpResponse<String> response = post("/tasks", "{\"title\":\"\"}");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body())
                .contains("\"message\":\"Validation failed\"")
                .contains("\"title\":\"Title cannot be empty\"");
    }

    @Test
    void malformedJson_returns400() throws Exception {
        HttpResponse<String> response = post("/tasks", "{\"title\":");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("isn't valid JSON");
    }

    @Test
    void nonNumericId_returns400() throws Exception {
        HttpResponse<String> response = get("/tasks/abc");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).contains("'abc' isn't a valid value");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri(path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}