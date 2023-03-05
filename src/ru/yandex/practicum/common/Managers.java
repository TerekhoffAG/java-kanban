package ru.yandex.practicum.common;

import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.interfaces.TaskManager;
import ru.yandex.practicum.services.HttpTaskManager;
import ru.yandex.practicum.services.InMemoryHistoryManager;
import ru.yandex.practicum.services.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultHttpTaskManager() {
        return new HttpTaskManager("http://localhost:8078");
    }
}
