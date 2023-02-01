package ru.yandex.practicum.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.TaskManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    protected T manager;

    protected Task createTask() {
        return new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now());
    }

    protected Epic createEpic() {
        return new Epic("эпик 1", "описание эпика 1");
    }

    protected SubTask createSubtask() {
        return new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 1);
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasksList = manager.getAllTask();

        assertEquals(1, tasksList.size());
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        List<Epic> epics = manager.getAllEpicTask();

        assertNotNull(epic.getStatus());
        assertEquals(TaskType.EPIC, epic.getType());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(0, epic.getDuration());
        assertNull(epic.getStartTime());
        assertEquals(1, epics.size());
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        List<SubTask> subTasks = manager.getAllSubTask();

        assertEquals(TaskType.SUBTASK, subTask.getType());
        assertEquals(List.of(subTask), subTasks);
        assertEquals(List.of(subTask.getId()), epic.getSubTaskIds());
    }

    @Test
    public void shouldReturnIntWhenNotFundEpic() {
        SubTask subTask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 6786);
        int result = manager.createSubTask(subTask);

        assertEquals(-1, result);
    }

    @Test
    public void shouldReturnIntWhenTaskCrossingDate() {
        Task task = createTask();
        Task noValidTask = new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 2, Instant.now());
        manager.createTask(task);
        int result = manager.createTask(noValidTask);

        assertEquals(-1, result);
    }

    @Test
    public void shouldReturnIntWhenSubTaskCrossingDate() {
        Task task = createTask();
        SubTask noValidSubTask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 2, Instant.now(), 2);
        manager.createTask(task);
        int result = manager.createSubTask(noValidSubTask);

        assertEquals(-1, result);
    }

    @Test
    public void shouldReturnListTasks() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasksList = manager.getAllTask();

        assertEquals(1, tasksList.size());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void shouldReturnListEpics() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        List<Epic> epics = manager.getAllEpicTask();

        assertEquals(1, epics.size());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getAllEpicTask().isEmpty());
    }

    @Test
    public void shouldReturnListSubTasks() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        List<SubTask> subTasks = manager.getAllSubTask();

        assertEquals(1, subTasks.size());
    }

    @Test
    public void shouldReturnEmptyListSubTasksIfNoSubTasks() {
        assertTrue(manager.getAllSubTask().isEmpty());
    }

    @Test
    public void shouldReturnListSubTasksByEpicId() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        List<SubTask> subTasks = manager.getAllSubTaskByEpicId(epic.getId());

        assertEquals(1, subTasks.size());
    }

    @Test
    public void shouldReturnEmptyListSubTasksIfNoEpicId() {
        assertTrue(manager.getAllSubTaskByEpicId(6).isEmpty());
    }

    @Test
    public void shouldReturnTasksById() {
        Task mockTask = createTask();
        manager.createTask(mockTask);
        Task task = manager.getTaskById(mockTask.getId());

        assertEquals(task, mockTask);
    }

    @Test
    public void shouldReturnEpicTaskById() {
        Epic mockEpic = createEpic();
        manager.createEpicTask(mockEpic);
        Epic epic = manager.getEpicTaskById(mockEpic.getId());

        assertEquals(epic, mockEpic);
    }

    @Test
    public void shouldReturnSubTasksById() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask mockSubTask = createSubtask();
        manager.createSubTask(mockSubTask);
        SubTask subTask = manager.getSubTaskById(mockSubTask.getId());

        assertEquals(subTask, mockSubTask);
    }

    @Test
    public void shouldUpdateTask() {
        Task mockTask = createTask();
        manager.createTask(mockTask);

        manager.updateTask(new Task(1, "задача 1", TaskType.TASK, TaskStatus.IN_PROGRESS, "описание задачи 1", 15, Instant.now()));
        Task task = manager.getTaskById(1);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);

        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateTaskIfNotId() {
        manager.updateTask(new Task(333, "задача 1", TaskType.TASK, TaskStatus.IN_PROGRESS, "описание задачи 1", 15, Instant.now().plusSeconds(1800)));
        Task task = manager.getTaskById(333);

        assertNull(task);
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        manager.updateEpicTask(new Epic(1, "эпик 2(new)", "описание эпика 2(new)", 15, Instant.now(), new ArrayList<>(List.of(2))));

        assertEquals("эпик 2(new)", manager.getEpicTaskById(1).getName());
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        manager.updateEpicTask(null);

        assertEquals(epic, manager.getEpicTaskById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNotId() {
        manager.updateEpicTask(new Epic(1, "эпик 2(new)", "описание эпика 2(new)", 15, Instant.now(), new ArrayList<>(List.of(2))));
        Epic epic = manager.getEpicTaskById(1);
        assertNull(epic);
    }

    @Test
    public void shouldUpdateSubTask() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        manager.updateSubTask(new SubTask(2, "подзадача 1-1(new)", TaskStatus.NEW, "описание подзадачи 1-1", 15, Instant.now().plusSeconds(3900), 1));

        assertEquals("подзадача 1-1(new)", manager.getSubTaskById(2).getName());
    }

    @Test
    public void shouldNotUpdateSubTaskIfNull() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subTask = createSubtask();
        manager.createSubTask(subTask);
        manager.updateSubTask(null);

        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTasks();

        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        manager.deleteAllEpicTasks();

        assertTrue(manager.getAllEpicTask().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subtask = createSubtask();
        manager.createSubTask(subtask);

        manager.deleteAllSubTasks();
        assertTrue(epic.getSubTaskIds().isEmpty());
        assertTrue(manager.getAllSubTask().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void shouldNotDeleteTaskByBadId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(243);

        assertEquals(List.of(task), manager.getAllTask());
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        manager.deleteEpicTaskById(epic.getId());

        assertTrue(manager.getAllEpicTask().isEmpty());
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        manager.deleteEpicTaskById(33);

        assertEquals(List.of(epic), manager.getAllEpicTask());
    }

    @Test
    public void shouldDeleteSubtaskById() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subtask = createSubtask();
        manager.createSubTask(subtask);
        manager.deleteSubTaskById(subtask.getId());

        assertEquals(List.of(), manager.getEpicTaskById(epic.getId()).getSubTaskIds());
    }

    @Test
    public void shouldNotDeleteSubtaskIfBadId() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subtask = createSubtask();
        manager.createSubTask(subtask);
        manager.deleteSubTaskById(33);

        assertEquals(List.of(subtask.getId()), manager.getEpicTaskById(epic.getId()).getSubTaskIds());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        SubTask subtask = new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 2);
        manager.createSubTask(subtask);
        manager.getTaskById(task.getId());
        manager.getEpicTaskById(epic.getId());
        manager.getSubTaskById(subtask.getId());
        List<Task> list = manager.getHistory();

        assertEquals(3, list.size());
        assertTrue(list.contains(task));
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

    @Test
    public void shouldAddCorrectlyStatusEpic() {
        Epic epic = createEpic();
        manager.createEpicTask(epic);
        manager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 1));
        manager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 10, null, 1));
        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(1).getStatus());

        manager.updateSubTask(new SubTask(2, "подзадача 1-1", TaskStatus.IN_PROGRESS, "описание подзадачи 1-1", 10, Instant.now().plusSeconds(900), 1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(1).getStatus());

        manager.updateSubTask(new SubTask(2, "подзадача 1-1", TaskStatus.DONE, "описание подзадачи 1-1", 1, Instant.now(), 1));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTaskById(1).getStatus());

        manager.updateSubTask(new SubTask(3, "подзадача 1-2", TaskStatus.DONE, "описание подзадачи 1-2", 1, Instant.now().plusSeconds(900), 1));
        assertEquals(TaskStatus.DONE, manager.getEpicTaskById(1).getStatus());

        manager.deleteAllSubTasks();
        assertEquals(TaskStatus.NEW, manager.getEpicTaskById(1).getStatus());
    }
}
