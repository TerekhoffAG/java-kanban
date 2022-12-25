package ru.yandex.practicum;

import ru.yandex.practicum.common.Managers;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager tasksManager = Managers.getDefault();

        System.out.println("Создание 2-х задач.");
        tasksManager.createTask(new Task("задача 1", TaskStatus.NEW, "описание задачи 1"));
        tasksManager.createTask(new Task("задача 2", TaskStatus.NEW, "описание задачи 2"));

        System.out.println("Создание 2-x эпика.");
        tasksManager.createEpicTask(new Epic("эпик 1", "описание эпика 1"));
        tasksManager.createEpicTask(new Epic("эпик 2", "описание эпика 2"));

        System.out.println("Создание подзадач.");
        tasksManager.createSubTask(new SubTask("подзадача 1-1", TaskStatus.NEW, "описание подзадачи 1-1", 3));
        tasksManager.createSubTask(new SubTask("подзадача 1-2", TaskStatus.NEW, "описание подзадачи 1-2", 3));
        tasksManager.createSubTask(new SubTask("подзадача 2-1", TaskStatus.NEW, "описание подзадачи 2-1", 4));

        System.out.println("Список задач.");
        System.out.println(tasksManager.getAllTask());
        System.out.println("Список эпиков.");
        System.out.println(tasksManager.getAllEpicTask());
        System.out.println("Список подзадач.");
        System.out.println(tasksManager.getAllSubTask());

        System.out.println("Изменение статуса задачи 1.");
        tasksManager.updateTask(new Task(1, "задача 1", TaskStatus.IN_PROGRESS, "описание задачи 1"));
        System.out.println("Изменение статуса задачи 2.");
        tasksManager.updateTask(new Task(2, "задача 2", TaskStatus.IN_PROGRESS, "описание задачи 2"));
        System.out.println("Изменение статуса подзадачи 1-1.");
        tasksManager.updateSubTask(new SubTask(5, "подзадача 1-1", TaskStatus.DONE, "описание подзадачи 1-1", 3));
        System.out.println("Изменение статуса подзадачи 2-1.");
        tasksManager.updateSubTask(new SubTask(7, "подзадача 2-1", TaskStatus.IN_PROGRESS, "описание подзадачи 2-1", 4));
        System.out.println("Изменение эпика 2.");
        tasksManager.updateEpicTask(new Epic(4, "эпик 2(new)", "описание эпика 2(new)", new ArrayList<>(List.of(7))));

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
        System.out.println("#########################");
        System.out.println("История просмотров задач.");
        System.out.println(tasksManager.getHistory());
        System.out.println("#########################");

        System.out.println("Список задач.");
        System.out.println(tasksManager.getAllTask());
        System.out.println("Список эпиков.");
        System.out.println(tasksManager.getAllEpicTask());
        System.out.println("Список подзадач.");
        System.out.println(tasksManager.getAllSubTask());

        System.out.println("Удаление задачи 2.");
        tasksManager.deleteTaskById(2);
        System.out.println("Удаление эпика 2.");
        tasksManager.deleteEpicTaskById(4);
        System.out.println("Удаление подзадачи 1-2.");
        tasksManager.deleteSubTaskById(6);

        System.out.println("Задач 1.");
        System.out.println(tasksManager.getTaskById(1));
        System.out.println("Эпик 1.");
        System.out.println(tasksManager.getEpicTaskById(3));
        System.out.println("Список подзадач эпика 1.");
        System.out.println(tasksManager.getAllSubTaskByEpicId(3));

        System.out.println("#########################");
        System.out.println("История просмотров задач.");
        System.out.println(tasksManager.getHistory());
        System.out.println("#########################");

        System.out.println("Удаление всех задач.");
        tasksManager.deleteAllTasks();
        System.out.println("Удаление всех эпиков.");
        tasksManager.deleteAllEpicTasks();
        System.out.println("Удаление всех подзадач.");
        tasksManager.deleteAllSubTasks();

        System.out.println("#########################");
        System.out.println("История просмотров задач.");
        System.out.println(tasksManager.getHistory());
        System.out.println("#########################");
    }
}
