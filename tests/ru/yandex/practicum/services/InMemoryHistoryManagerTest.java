package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Task;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    InMemoryTaskManager taskManager;

    protected Task createTask() {
        return new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now());
    }

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldCorrectlyAddTaskInHistory() {
        historyManager.add(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now()));
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
    }

    @Test
    void shouldNotAddTaskInHistoryIfNull() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void shouldGetEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void shouldNotDoubleAddTaskInHistory() {
        Task task = createTask();
        taskManager.createTask(task);
        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(taskManager.getTaskById(1));

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskInHistory() {
        Task task = createTask();
        taskManager.createTask(task);
        historyManager.add(taskManager.getTaskById(1));
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldCorrectlyRemoveTaskInHistory() {
        taskManager.createTask(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now()));
        taskManager.createTask(new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.now().plusSeconds(1200)));
        taskManager.createTask(new Task("задача 3", TaskType.TASK, TaskStatus.NEW, "описание задачи 3", 1, Instant.now().plusSeconds(2400)));
        taskManager.createTask(new Task("задача 4", TaskType.TASK, TaskStatus.NEW, "описание задачи 4", 1, Instant.now().plusSeconds(3600)));
        taskManager.createTask(new Task("задача 5", TaskType.TASK, TaskStatus.NEW, "описание задачи 5", 1, Instant.now().plusSeconds(4800)));

        historyManager.add(taskManager.getTaskById(1));
        historyManager.add(taskManager.getTaskById(2));
        historyManager.add(taskManager.getTaskById(3));
        historyManager.add(taskManager.getTaskById(4));
        historyManager.add(taskManager.getTaskById(5));

        assertEquals(5, historyManager.getHistory().size());
        assertEquals(1, historyManager.getHistory().get(0).getId());
        assertEquals(2, historyManager.getHistory().get(1).getId());
        assertEquals(3, historyManager.getHistory().get(2).getId());
        assertEquals(4, historyManager.getHistory().get(3).getId());
        assertEquals(5, historyManager.getHistory().get(4).getId());

        historyManager.remove(1);
        assertEquals(2, historyManager.getHistory().get(0).getId());
        assertEquals(3, historyManager.getHistory().get(1).getId());
        assertEquals(4, historyManager.getHistory().get(2).getId());
        assertEquals(5, historyManager.getHistory().get(3).getId());

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().get(0).getId());
        assertEquals(4, historyManager.getHistory().get(1).getId());
        assertEquals(5, historyManager.getHistory().get(2).getId());

        historyManager.remove(5);
        assertEquals(2, historyManager.getHistory().get(0).getId());
        assertEquals(4, historyManager.getHistory().get(1).getId());
    }

}