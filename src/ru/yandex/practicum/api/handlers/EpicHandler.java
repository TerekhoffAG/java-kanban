package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.InstantAdapter;
import ru.yandex.practicum.common.HttpMethods;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;

public class EpicHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response = "";
        HttpMethods method = HttpMethods.valueOf(exchange.getRequestMethod());
        String query = exchange.getRequestURI().getQuery();
        int id;

        switch (method){
            case GET:
                if (query == null) {
                    ArrayList<Epic> epics = taskManager.getAllEpicTask();
                    response = epics.isEmpty() ? "Список эпиков пуст." : gson.toJson(epics);
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    Epic epic = taskManager.getEpicTaskById(id);
                    response = epic != null ? gson.toJson(epic) : "Эпик не найден.";
                }

                statusCode = 200;
                break;
            case POST:
                InputStream stream = exchange.getRequestBody();
                String requestBody = new String(stream.readAllBytes(), DEFAULT_CHARSET);
                Epic dto = gson.fromJson(requestBody, Epic.class);
                id = dto.getId();

                if (id == 0) {
                    int result = taskManager.createEpicTask(dto);
                    response = "Создан эпик с id " + result;
                } else {
                    taskManager.updateEpicTask(dto);
                    response = "Эпик с id " + id + " обновлён.";
                }

                statusCode = 201;
                break;
            case DELETE:
                if (query == null) {
                    taskManager.deleteAllEpicTasks();
                    response = "Удалены все эпики.";
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    taskManager.deleteEpicTaskById(id);
                    response = "Удалён эпик с id " + id;
                }

                statusCode = 200;
                break;
            default:
                response = "Получен некорректный эндпоинт.";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
