package ru.yandex.practicum.services;

import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.HistoryManager;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_TASKS = 10;
    private final LinkedList<Task> historyTasks = new LinkedList<>();

    /**
     * Добавляет задачу в историю просмотров.
     */
    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyTasks.size() >= MAX_TASKS) {
                historyTasks.removeFirst();
            }
            historyTasks.add(task);
        }
    }

    /**
     * Получает историю просмотров задач.
     */
    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }
}
