package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.InstantAdapter;
import ru.yandex.practicum.common.HttpMethods;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.interfaces.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;

public class SubtaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public SubtaskHandler (TaskManager taskManager) {
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
                    ArrayList<SubTask> subTasks = taskManager.getAllSubTask();
                    response = subTasks.isEmpty() ? "Список подзадач пуст." : gson.toJson(subTasks);
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    SubTask subTask = taskManager.getSubTaskById(id);
                    response = subTask != null ? gson.toJson(subTask) : "Подзадача не найдена.";
                }

                statusCode = 200;
                break;
            case POST:
                InputStream stream = exchange.getRequestBody();
                String requestBody = new String(stream.readAllBytes(), DEFAULT_CHARSET);
                SubTask dto = gson.fromJson(requestBody, SubTask.class);
                id = dto.getId();

                if (id == 0) {
                    int result = taskManager.createSubTask(dto);
                    if (result != -1) {
                        response = "Создана подзадача id " + result;
                    } else {
                        response = "Не возможно создать подзадачу с заданными параметрами.";
                    }
                } else {
                    taskManager.updateSubTask(dto);
                    response = "Подзадача с id " + id + " обновлена.";
                }

                statusCode = 201;
                break;
            case DELETE:
                if (query == null) {
                    taskManager.deleteAllSubTasks();
                    response = "Удалены все подзадачи.";
                } else {
                    id = Integer.parseInt(query.split("id=")[1]);
                    taskManager.deleteSubTaskById(id);
                    response = "Удалена подзадача id " + id;
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
