package ru.yandex.practicum.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.adapters.InstantAdapter;
import ru.yandex.practicum.api.KVTaskClient;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;

import java.io.File;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public HttpTaskManager(String url) {
        super(new File("resources/task.csv"));
        client = new KVTaskClient(url);
        loadFromServer();
    }

    private void loadFromServer() {
        Type taskType = new TypeToken<List<Task>>() {}.getType();
        String jsonTaskStr = client.load(TaskType.TASK.name());
        if (!jsonTaskStr.equals("")) {
            ArrayList<Task> tasks = gson.fromJson(jsonTaskStr, taskType);
            for (Task task : tasks) {
                addTask(task);
            }
        }

        Type epicType = new TypeToken<List<Epic>>() {}.getType();
        String jsonEpicStr = client.load(TaskType.EPIC.name());
        if (!jsonEpicStr.equals("")) {
            ArrayList<Epic> epics = gson.fromJson(jsonEpicStr, epicType);
            for (Epic epic : epics) {
                epic.clearSubTaskIds();
                addEpic(epic);
            }
        }

        Type subTaskType = new TypeToken<List<SubTask>>() {}.getType();
        String jsonSubTaskStr = client.load(TaskType.SUBTASK.name());
        if (!jsonSubTaskStr.equals("")) {
            ArrayList<SubTask> subTasks = gson.fromJson(jsonSubTaskStr, subTaskType);
            for (SubTask subTask : subTasks) {
                addSubTask(subTask);
            }
        }

        Type historyType = new TypeToken<List<Integer>>() {}.getType();
        String jsonHistoryStr = client.load("HISTORY");
        if (!jsonHistoryStr.equals("")) {
            ArrayList<Integer> history = gson.fromJson(jsonHistoryStr, historyType);
            for (Integer id : history) {
                addHistory(id);
            }
        }
    }

    @Override
    public void save() {
        client.put(TaskType.TASK.name(), gson.toJson(tasks.values()));
        client.put(TaskType.EPIC.name(), gson.toJson(epicTasks.values()));
        client.put(TaskType.SUBTASK.name(), gson.toJson(subTasks.values()));
        client.put("HISTORY", gson.toJson(historyManager.getHistory()
                .stream().map(Task::getId).collect(Collectors.toList())));
    }
}
