package ru.yandex.practicum;

import ru.yandex.practicum.api.HttpTaskServer;
import ru.yandex.practicum.api.KVServer;
import ru.yandex.practicum.api.KVTaskClient;
import ru.yandex.practicum.common.Managers;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.TaskManager;
import ru.yandex.practicum.services.FileBackedTasksManager;
import ru.yandex.practicum.services.HttpTaskManager;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
/////////////////////////////////////////////////////////////////////////////////////
// TODO: Раз комментировать весь код при загрузке данных из файла ./resources/task.csv
/////////////////////////////////////////////////////////////////////////////////////
//        File file = new File("resources/task.csv");
//        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
//
//        System.out.println("Создание 2-х задач.");
//        tasksManager.createTask(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.now()));
//        tasksManager.createTask(new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.now().plusSeconds(120)));
//
//        System.out.println("Создание 2-x эпика.");
//        tasksManager.createEpicTask(new Epic("эпик 1", "описание эпика 1"));
//        tasksManager.createEpicTask(new Epic("эпик 2", "описание эпика 2"));
//
//        System.out.println("Создание 3-x подзадач.");
//        tasksManager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 10, null, 3));
//        tasksManager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 10, null, 3));
//        tasksManager.createSubTask(new SubTask("подзадача 2-1", TaskStatus.NEW, "описание подзадачи 2-1", 2, Instant.now().plusSeconds(300), 4));
//
//        System.out.println("Изменение статуса задачи 1.");
//        tasksManager.updateTask(new Task(1, "задача 1", TaskType.TASK, TaskStatus.IN_PROGRESS, "описание задачи 1", 15, Instant.now()));
//        System.out.println("Изменение статуса задачи 2.");
//        tasksManager.updateTask(new Task(2, "задача 2", TaskType.TASK, TaskStatus.IN_PROGRESS, "описание задачи 2", 20, Instant.now().plusSeconds(1800)));
//        System.out.println("Изменение статуса подзадачи 1-1.");
//        tasksManager.updateSubTask(new SubTask(5, "подзадача 1-1", TaskStatus.DONE, "описание подзадачи 1-1", 15, Instant.now().plusSeconds(900), 3));
//        System.out.println("Изменение статуса подзадачи 2-1.");
//        tasksManager.updateSubTask(new SubTask(7, "подзадача 2-1", TaskStatus.IN_PROGRESS, "описание подзадачи 2-1", 20, Instant.now(), 4));
//        System.out.println("Изменение эпика 2.");
//        tasksManager.updateEpicTask(new Epic(4, "эпик 2(new)", "описание эпика 2(new)", 15, Instant.now(), new ArrayList<>(List.of(7))));
//
//        System.out.println("Список задач.");
//        System.out.println(tasksManager.getAllTask());
//        System.out.println("Список эпиков.");
//        System.out.println(tasksManager.getAllEpicTask());
//        System.out.println("Список подзадач.");
//        System.out.println(tasksManager.getAllSubTask());
//
//        tasksManager.getTaskById(1);
//        tasksManager.getTaskById(2);
//        tasksManager.getTaskById(1);
//        tasksManager.getEpicTaskById(3);
//        tasksManager.getEpicTaskById(4);
//        tasksManager.getEpicTaskById(3);
//        tasksManager.getSubTaskById(7);
//        tasksManager.getSubTaskById(6);
//        tasksManager.getSubTaskById(5);
//        tasksManager.getSubTaskById(7);
//        tasksManager.getSubTaskById(5);
//        System.out.println("#########################");
//        System.out.println("История просмотров задач.");
//        System.out.println(tasksManager.getHistory());
//        System.out.println("#########################");
//
//        System.out.println("#########################");
//        System.out.println("Список задач в порядке приоритета.");
//        System.out.println(tasksManager.getPrioritizedTasks());
//        System.out.println("#########################");

        new KVServer().start();

        HttpTaskManager tasksManager = new HttpTaskManager("http://localhost:8078");
        tasksManager.createTask(new Task("задача 1", TaskType.TASK, TaskStatus.NEW, "описание задачи 1", 2, Instant.ofEpochMilli(1234567890)));
        tasksManager.createTask(new Task("задача 2", TaskType.TASK, TaskStatus.NEW, "описание задачи 2", 1, Instant.now().plusSeconds(120)));

        tasksManager.createEpicTask(new Epic("эпик 1", "описание эпика 1"));
        tasksManager.createEpicTask(new Epic("эпик 2", "описание эпика 2"));

        tasksManager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 1, null, 3));
        tasksManager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 1, null, 3));
        tasksManager.createSubTask(new SubTask("подзадача 2-1", TaskStatus.NEW, "описание подзадачи 2-1", 2, Instant.now().plusSeconds(300), 4));

        tasksManager.getTaskById(1);
        tasksManager.getTaskById(2);
        tasksManager.getTaskById(1);
        tasksManager.getEpicTaskById(3);
        tasksManager.getEpicTaskById(4);
        tasksManager.getEpicTaskById(3);
        tasksManager.getSubTaskById(7);
        tasksManager.getSubTaskById(6);
        tasksManager.getSubTaskById(5);
        tasksManager.getSubTaskById(7);
        tasksManager.getSubTaskById(5);

        HttpTaskManager newTasksManager = new HttpTaskManager("http://localhost:8078");

        System.out.println("Список задач.");
        System.out.println(newTasksManager.getAllTask());
        System.out.println(newTasksManager.getAllEpicTask());
        System.out.println(newTasksManager.getAllSubTask());
        System.out.println("История просмотров задач.");
        System.out.println(newTasksManager.getHistory());

        new HttpTaskServer().start();
    }
}
