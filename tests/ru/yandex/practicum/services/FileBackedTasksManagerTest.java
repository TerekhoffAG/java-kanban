package ru.yandex.practicum.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public static final Path path = Path.of("test.csv");
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now());
        manager.createTask(task);
        Epic epic = new Epic("эпик 1", "описание эпика 1");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 2);
        manager.createSubTask(subTask);
        manager.getTaskById(1);
        manager.getEpicTaskById(2);
        manager.getSubTaskById(3);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(List.of(task), fileManager.getAllTask());
        assertEquals(List.of(epic), fileManager.getAllEpicTask());
        assertEquals(List.of(subTask), fileManager.getAllSubTask());
        assertEquals(3, fileManager.getHistory().size());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now());
        manager.createTask(task);
        Epic epic = new Epic("эпик 1", "описание эпика 1");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 2);
        manager.createSubTask(subTask);
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubTasks();
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);

        assertTrue(fileManager.getAllTask().isEmpty());
        assertTrue(fileManager.getAllEpicTask().isEmpty());
        assertTrue(fileManager.getAllSubTask().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        Task task = new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now());
        manager.createTask(task);
        Epic epic = new Epic("эпик 1", "описание эпика 1");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 2);
        manager.createSubTask(subTask);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);

        assertTrue(fileManager.getHistory().isEmpty());
    }
}
