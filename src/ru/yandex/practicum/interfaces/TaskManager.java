package ru.yandex.practicum.interfaces;

import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    /**
     * Создаёт задачу.
     */
    int createTask(Task task);

    /**
     * Создаёт эпик.
     */
    int createEpicTask(Epic epicTask);

    /**
     * Создаёт подзадачу.
     */
    int createSubTask(SubTask subTask);

    /**
     * Добавляет задачу.
     */
    void addTask(Task task);

    /**
     * Добавляет эпик.
     */
    void addEpic(Epic epicTask);

    /**
     * Добавляет подзадачу.
     */
    void addSubTask(SubTask subTask);

    /**
     * Получает список всех задачь.
     */
    ArrayList<Task> getAllTask();

    /**
     * Получает список всех эпиков.
     */
    ArrayList<Epic> getAllEpicTask();

    /**
     * Получает список всех подзадач.
     */
    ArrayList<SubTask> getAllSubTask();

    /**
     * Получает список подчазачь эпика.
     */
    ArrayList<SubTask> getAllSubTaskByEpicId(int id);

    /**
     * Получает задачу по id.
     */
    Task getTaskById(int id);

    /**
     * Получает эпик по id.
     */
    Epic getEpicTaskById(int id);

    /**
     * Получает подзадачу по id.
     */
    SubTask getSubTaskById(int id);

    /**
     * Обновляет задачу.
     */
    void updateTask(Task task);

    /**
     * Обновляет эпик.
     */
    void updateEpicTask(Epic epicTask);

    /**
     * Обновляет подзадачу.
     */
    void updateSubTask(SubTask subTask);

    /**
     * Удаляет все задачи.
     */
    void deleteAllTasks();

    /**
     * Удаляет все эпики.
     */
    void deleteAllEpicTasks();

    /**
     * Удаляет все подзадачи.
     */
    void deleteAllSubTasks();

    /**
     * Удаляет задачу по id.
     */
    void deleteTaskById(int id);

    /**
     * Удаляет эпик по id.
     */
    void deleteEpicTaskById(int id);

    /**
     * Удаляет подзадачу по id.
     */
    void deleteSubTaskById(Integer id);

    /**
     * Добавляет задачи в историю простотра по id.
     */
    void addHistory(int id);

    /**
     * Получает историю просмотров задач.
     */
    List<Task> getHistory();

    /**
     * Получает инстанс менеджера истории.
     */
    HistoryManager getHistoryManager();

    /**
     * Получает список задач в порядке приоритета.
     */
    List<Task> getPrioritizedTasks();
}
