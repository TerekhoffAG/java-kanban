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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public TaskHandler(TaskManager taskManager) {
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
                    ArrayList<Task> tasks = taskManager.getAllTask();
                    response = tasks.isEmpty() ? "Список задач пуст." : gson.toJson(tasks);
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    Task task = taskManager.getTaskById(id);
                    response = task != null ? gson.toJson(task) : "Задача не найдена.";
                }

                statusCode = 200;
                break;
            case POST:
                InputStream stream = exchange.getRequestBody();
                String requestBody = new String(stream.readAllBytes(), DEFAULT_CHARSET);
                Task dto = gson.fromJson(requestBody, Task.class);
                id = dto.getId();

                if (id == 0) {
                    int result = taskManager.createTask(dto);
                    if (result != -1) {
                        response = "Создана задача с id " + result;
                    } else {
                        response = "В заданное время назначено выполнение другой задачи.";
                    }
                } else {
                    taskManager.updateTask(dto);
                    response = "Задача с id " + id + " обновлена";
                }

                statusCode = 201;
                break;
            case DELETE:
                if (query == null) {
                    taskManager.deleteAllTasks();
                    response = "Удалены все задачи.";
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    taskManager.deleteTaskById(id);
                    response = "Удалена задача с id " + id;
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
