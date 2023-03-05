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

class HttpTaskManagerTest {
    protected KVServer server;
    HttpTaskManager tasksManager;

    HttpTaskManagerTest() throws IOException {
        server = new KVServer();
    }

    @BeforeEach
    public void beforeEach() {
        server.start();

        tasksManager = new HttpTaskManager("http://localhost:8078");
        tasksManager.createTask(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 1, Instant.ofEpochMilli(1234567890)));
        tasksManager.createTask(new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.ofEpochMilli(1234567890).plusSeconds(120)));
        tasksManager.createEpicTask(new Epic("эпик 1", "описание эпика 1"));
        tasksManager.createEpicTask(new Epic("эпик 2", "описание эпика 2"));
        tasksManager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 1, null, 3));
        tasksManager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 1, null, 3));
        tasksManager.createSubTask(new SubTask("подзадача 2-1", TaskStatus.NEW, "описание подзадачи 2-1", 1, Instant.ofEpochMilli(1234567890).plusSeconds(300), 4));
        tasksManager.getTaskById(1);
        tasksManager.getTaskById(2);
        tasksManager.getTaskById(1);
        tasksManager.getEpicTaskById(3);
        tasksManager.getEpicTaskById(4);
        tasksManager.getEpicTaskById(3);
        tasksManager.getSubTaskById(7);
        tasksManager.getSubTaskById(6);
        tasksManager.getSubTaskById(5);
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        HttpTaskManager newTasksManager = new HttpTaskManager("http://localhost:8078");

        assertEquals(tasksManager.getAllTask().size(), newTasksManager.getAllTask().size());
        assertEquals(tasksManager.getAllEpicTask().size(), newTasksManager.getAllEpicTask().size());
        assertEquals(tasksManager.getAllSubTask().size(), newTasksManager.getAllSubTask().size());
        assertEquals(tasksManager.getHistory().size(), newTasksManager.getHistory().size());
        assertEquals(tasksManager.getTaskById(1), newTasksManager.getTaskById(1));
        assertEquals(tasksManager.getTaskById(2), newTasksManager.getTaskById(2));
        assertEquals(tasksManager.getEpicTaskById(3), newTasksManager.getEpicTaskById(3));
        assertEquals(tasksManager.getEpicTaskById(4), newTasksManager.getEpicTaskById(4));
        assertEquals(tasksManager.getSubTaskById(5), newTasksManager.getSubTaskById(5));
        assertEquals(tasksManager.getSubTaskById(7), newTasksManager.getSubTaskById(7));
    }

    @Test
    public void shouldSaveAndLoadNullValueFields() {
        HttpTaskManager newTasksManager = new HttpTaskManager("http://localhost:8078");

        assertNull(newTasksManager.getEpicTaskById(3).getEndTime());
        assertNull(newTasksManager.getEpicTaskById(3).getStartTime());
        assertNull(newTasksManager.getSubTaskById(5).getStartTime());
    }
}