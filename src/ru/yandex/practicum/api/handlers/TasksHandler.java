package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.InstantAdapter;
import ru.yandex.practicum.common.HttpMethods;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.common.HttpMethods.GET;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response = "";
        HttpMethods method = HttpMethods.valueOf(exchange.getRequestMethod());
        if (method == GET) {
            statusCode = 200;
            List<Task> taskList = taskManager.getPrioritizedTasks();
            response = taskList.isEmpty() ? "Список задач пуст." : gson.toJson(taskList);
        } else {
            response = "Получен некорректный эндпоинт.";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
