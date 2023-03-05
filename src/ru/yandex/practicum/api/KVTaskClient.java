package ru.yandex.practicum.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String apiToken;
    private final String url;

    public KVTaskClient(String serverURL) {
        HttpResponse<String> response = null;
        this.url = serverURL;
        URI uri = URI.create(this.url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        this.apiToken = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.url + "/save/" + key + "?API_TOKEN=" + this.apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load (String key) {
        HttpResponse<String> response = null;
        URI uri = URI.create(this.url + "/load/" + key + "?API_TOKEN=" + this.apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(DEFAULT_CHARSET));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return response.statusCode() == 200 ? response.body() : "";
    }
}
