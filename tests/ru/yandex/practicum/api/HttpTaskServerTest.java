package ru.yandex.practicum.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.adapters.InstantAdapter;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpClient client;
    private static HttpTaskServer taskServer;
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    @BeforeEach
    void startServer() throws IOException {
        client = HttpClient.newHttpClient();
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void shouldGetTasks() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task1 = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890));
        Task task2 = new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.ofEpochMilli(1234767890));

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(2, arrayTasks.size());

            Type taskType = new TypeToken<List<Task>>() {}.getType();
            ArrayList<Task> tasks = gson.fromJson(response.body(), taskType);

            task1.setId(1);
            task2.setId(2);
            assertEquals(List.of(task1, task2), tasks);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic1 = new Epic("эпик 1", "описание эпика 1");
        Epic epic2 = new Epic("эпик 2", "описание эпика 2");

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();

        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());

            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            JsonArray arrEpics = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(2, arrEpics.size());

            Type taskType = new TypeToken<List<Epic>>() {}.getType();
            ArrayList<Epic> epics = gson.fromJson(response.body(), taskType);

            epic1.setId(1);
            epic2.setId(2);
            assertEquals(List.of(epic1, epic2), epics);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasksTest() {
        client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                SubTask subtask1 = new SubTask(
                        "подзадача 1-1",
                        TaskStatus.NEW,
                        "описание подзадачи 1-1",
                        1,
                        null,
                        1
                );
                SubTask subtask2 = new SubTask(
                        "подзадача 1-2",
                        TaskStatus.NEW,
                        "описание подзадачи 1-2",
                        1,
                        null,
                        1
                );
                url = URI.create("http://localhost:8080/tasks/subtask/");

                HttpRequest request1 = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                        .build();
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                        .build();

                client.send(request1, HttpResponse.BodyHandlers.ofString());
                client.send(request2, HttpResponse.BodyHandlers.ofString());

                HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode());
                JsonArray subTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(2, subTasks.size());

                Type subtaskType = new TypeToken<List<SubTask>>() {}.getType();
                ArrayList<SubTask> subtasks = gson.fromJson(response.body(), subtaskType);

                subtask1.setId(2);
                subtask2.setId(3);
                assertEquals(List.of(subtask1, subtask2), subtasks);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                url = URI.create("http://localhost:8080/tasks/task/?id=1");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                task.setId(1);
                Task dtoTask = gson.fromJson(response.body(), Task.class);
                assertEquals(dtoTask, task);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                url = URI.create("http://localhost:8080/tasks/epic/?id=1");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                epic.setId(1);
                Task dtoEpic = gson.fromJson(response.body(), Epic.class);
                assertEquals(dtoEpic, epic);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtaskById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            SubTask subtask = new SubTask(
                    "подзадача 1-1",
                    TaskStatus.NEW,
                    "описание подзадачи 1-1",
                    1,
                    null,
                    1
            );
            url = URI.create("http://localhost:8080/tasks/subtask/");

            request = HttpRequest.newBuilder().uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                subtask.setId(2);
                Task dtoSubtask = gson.fromJson(response.body(), SubTask.class);
                assertEquals(dtoSubtask, subtask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateTask() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                task.setStatus(TaskStatus.IN_PROGRESS);
                task.setId(1);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create("http://localhost:8080/tasks/task/?id=1");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateEpic() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                epic.setDescription("Новое описание эпика 1");
                epic.setId(1);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create("http://localhost:8080/tasks/epic/?id=1");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSubtask() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                SubTask subtask = new SubTask(
                        "подзадача 1-1",
                        TaskStatus.NEW,
                        "описание подзадачи 1-1",
                        1,
                        null,
                        1
                );
                url = URI.create("http://localhost:8080/tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    subtask.setStatus(TaskStatus.IN_PROGRESS);
                    subtask.setId(2);
                    request = HttpRequest.newBuilder()
                            .uri(url)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                    url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    SubTask responseTask = gson.fromJson(response.body(), SubTask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTasks() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список задач пуст.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpics() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список эпиков пуст.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtasks() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            SubTask subtask = new SubTask(
                    "подзадача 1-1",
                    TaskStatus.NEW,
                    "описание подзадачи 1-1",
                    1,
                    null,
                    1
            );
            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/epic/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals("Список подзадач пуст.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/task/?id=1");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача не найдена.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/epic/?id=1");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Эпик не найден.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtaskById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("эпик 1", "описание эпика 1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());

            SubTask subtask = new SubTask(
                    "подзадача 1-1",
                    TaskStatus.NEW,
                    "описание подзадачи 1-1",
                    1,
                    null,
                    1
            );
            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Подзадача не найдена.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}