package ru.yandex.practicum.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.api.KVServer;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    protected KVServer server;
//    HttpTaskManager tasksManager;

    HttpTaskManagerTest() throws IOException {
        server = new KVServer();
    }

    public void createMockTasks() {
        manager.createTask(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890)));
        manager.createTask(new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.ofEpochMilli(1234567890).plusSeconds(120)));
        manager.createEpicTask(new Epic("эпик 1", "описание эпика 1"));
        manager.createEpicTask(new Epic("эпик 2", "описание эпика 2"));
        manager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 1, null, 3));
        manager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 1, null, 3));
        manager.createSubTask(new SubTask("подзадача 2-1", TaskStatus.NEW, "описание подзадачи 2-1", 1, Instant.ofEpochMilli(1234567890).plusSeconds(300), 4));
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getEpicTaskById(3);
        manager.getEpicTaskById(4);
        manager.getEpicTaskById(3);
        manager.getSubTaskById(7);
        manager.getSubTaskById(6);
        manager.getSubTaskById(5);
    }

    @BeforeEach
    public void beforeEach() {
        server.start();

        manager = new HttpTaskManager("http://localhost:8078");
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        createMockTasks();
        HttpTaskManager newTasksManager = new HttpTaskManager("http://localhost:8078");

        assertEquals(manager.getAllTask().size(), newTasksManager.getAllTask().size());
        assertEquals(manager.getAllEpicTask().size(), newTasksManager.getAllEpicTask().size());
        assertEquals(manager.getAllSubTask().size(), newTasksManager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), newTasksManager.getHistory().size());
        assertEquals(manager.getTaskById(1), newTasksManager.getTaskById(1));
        assertEquals(manager.getTaskById(2), newTasksManager.getTaskById(2));
        assertEquals(manager.getEpicTaskById(3), newTasksManager.getEpicTaskById(3));
        assertEquals(manager.getEpicTaskById(4), newTasksManager.getEpicTaskById(4));
        assertEquals(manager.getSubTaskById(5), newTasksManager.getSubTaskById(5));
        assertEquals(manager.getSubTaskById(7), newTasksManager.getSubTaskById(7));
    }

    @Test
    public void shouldSaveAndLoadNullValueFields() {
        createMockTasks();
        HttpTaskManager newTasksManager = new HttpTaskManager("http://localhost:8078");

        assertNull(newTasksManager.getEpicTaskById(3).getEndTime());
        assertNull(newTasksManager.getEpicTaskById(3).getStartTime());
        assertNull(newTasksManager.getSubTaskById(5).getStartTime());
    }
}