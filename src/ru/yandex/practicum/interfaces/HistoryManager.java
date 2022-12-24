package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.entities.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Добавляет задачу в историю просмотров.
     */
    void add(Task task);

    /**
     * Удаляет задачу из истории просмотров.
     */
    void remove(int id);

    /**
     * Получает историю просмотров задач.
     */
    List<Task> getHistory();
}
