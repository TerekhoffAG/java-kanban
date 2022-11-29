package ru.yandex.practicum.services;

import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_TASKS = 10;
    private final List<Task> historyTasks = new ArrayList<>();

    /**
     * Добавляет задачу в историю просмотров.
     */
    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyTasks.size() >= MAX_TASKS) {
                historyTasks.remove(0);
            }
            historyTasks.add(task);
        }
    }

    /**
     * Получает историю просмотров задач.
     */
    @Override
    public List<Task> getHistory() {
        if (historyTasks.isEmpty()) {
            System.out.print("История просмотров пуста.");
        }
        return historyTasks;
    }
}
