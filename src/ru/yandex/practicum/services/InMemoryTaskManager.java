package ru.yandex.practicum.services;

import ru.yandex.practicum.common.Managers;
import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.interfaces.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int generatedId = 0;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    private void updateEpicTaskStatus(Epic epic) {
        if (epicTasks.containsKey(epic.getId())) {
            ArrayList<Integer> subTasksIds = epic.getSubTaskIds();
            ArrayList<TaskStatus> statusList = new ArrayList<>();

            if (subTasksIds.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                for(Integer id : subTasksIds) {
                    TaskStatus status = subTasks.get(id).getStatus();

                    if (status == TaskStatus.IN_PROGRESS) {
                        epic.setStatus(TaskStatus.IN_PROGRESS);
                        return;
                    }

                    statusList.add(status);
                }

                if (statusList.contains(TaskStatus.DONE) && !statusList.contains(TaskStatus.NEW)) {
                    epic.setStatus(TaskStatus.DONE);
                } else if (statusList.contains(TaskStatus.NEW) && !statusList.contains(TaskStatus.DONE)) {
                    epic.setStatus(TaskStatus.NEW);
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик не найден.");
        }
    }

    private int generateId() {
        return ++generatedId;
    }

    protected void setGeneratedId(int id) {
        this.generatedId = id;
    }

    /**
     * Получает эпик по id, без сохранения в историю просмотров.
     */
    protected Epic getEpicNoHistory(int id) {
        return epicTasks.get(id);
    }

    /**
     * Создаёт задачу.
     */
    @Override
    public int createTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);

        return newId;
    }

    /**
     * Создаёт эпик.
     */
    @Override
    public int createEpicTask(Epic epicTask) {
        int newId = generateId();
        epicTask.setId(newId);
        this.epicTasks.put(newId, epicTask);

        return newId;
    }

    /**
     * Создаёт подзадачу.
     */
    @Override
    public int createSubTask(SubTask subTask) {
        int newId = generateId();
        subTask.setId(newId);
        Epic epic = epicTasks.get(subTask.getEpicTaskId());

        if (epic == null) {
            System.out.println("Подзадача не создана, не найден эпик!");
            return -1;
        }

        this.subTasks.put(newId, subTask);
        epic.addSubTaskId(newId);
        updateEpicTaskStatus(epic);

        return newId;
    }

    /**
     * Добавляет задачу.
     */
    @Override
    public void addTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    /**
     * Добавляет эпик.
     */
    @Override
    public void addEpic(Epic epic) {
        int id = epic.getId();
        epicTasks.put(id, epic);
    }

    /**
     * Добавляет подзадачу.
     */
    @Override
    public void addSubTask(SubTask subTask) {
        int id = subTask.getId();
        subTasks.put(id, subTask);
        Epic epic = epicTasks.get(subTask.getEpicTaskId());
        epic.addSubTaskId(id);
    }

    /**
     * Получает список всех задачь.
     */
    @Override
    public ArrayList<Task> getAllTask() {
        if(tasks.isEmpty()) {
            System.out.println("Список задач пуст.");
            return new ArrayList<>();
        }

        return new ArrayList<>(tasks.values());
    }

    /**
     * Получает список всех эпиков.
     */
    @Override
    public ArrayList<Epic> getAllEpicTask() {
        if(epicTasks.isEmpty()) {
            System.out.println("Список эпиков пуст.");
            return new ArrayList<>();
        }

        return new ArrayList<>(epicTasks.values());
    }

    /**
     * Получает список всех подзадач.
     */
    @Override
    public ArrayList<SubTask> getAllSubTask() {
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст.");
            return new ArrayList<>();
        }

        return new ArrayList<>(subTasks.values());
    }

    /**
     * Получает список подчазачь эпика.
     */
    @Override
    public ArrayList<SubTask> getAllSubTaskByEpicId(int id) {
        ArrayList<SubTask> subTasksForEpic = new ArrayList<>();

        if (epicTasks.containsKey(id)) {
            ArrayList<Integer> subTasksIds = epicTasks.get(id).getSubTaskIds();

            for (Integer subTaskId : subTasksIds) {
                subTasksForEpic.add(subTasks.get(subTaskId));
            }

        } else {
            System.out.println("Эпик не найден!");
        }

        return subTasksForEpic;
    }

    /**
     * Получает задачу по id.
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);

        return task;
    }

    /**
     * Получает эпик по id.
     */
    @Override
    public Epic getEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        historyManager.add(epic);

        return epic;
    }

    /**
     * Получает подзадачу по id.
     */
    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);

        return subTask;
    }

    /**
     * Обновляет задачу.
     */
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            int id = task.getId();

            if (tasks.containsKey(id)) {
                tasks.put(id, task);
            } else {
                System.out.println("Задача не найдена.");
            }
        } else {
            System.out.println("Ошибка обновления задачи.");
        }
    }

    /**
     * Обновляет эпик.
     */
    @Override
    public void updateEpicTask(Epic epicTask) {
        if (epicTask != null) {
            int id = epicTask.getId();

            if (epicTasks.containsKey(id)) {
                epicTasks.put(id, epicTask);
                updateEpicTaskStatus(epicTasks.get(id));
            } else {
                System.out.println("Эпик не найден.");
            }
        } else {
            System.out.println("Ошибка обновления эпика.");
        }
    }

    /**
     * Обновляет подзадачу.
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            int id = subTask.getId();

            if (subTasks.containsKey(id)) {
                subTasks.put(id, subTask);
                Epic epic = epicTasks.get(subTask.getEpicTaskId());
                updateEpicTaskStatus(epic);
            } else {
                System.out.println("Подзадача не найдена.");
            }
        } else {
            System.out.println("Ошибка обновления подзадача.");
        }
    }

    /**
     * Удаляет все задачи.
     */
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    /**
     * Удаляет все эпики.
     */
    @Override
    public void deleteAllEpicTasks() {
        for (Epic epic : epicTasks.values()) {
            ArrayList<Integer> idsList = epic.getSubTaskIds();

            for (Integer id : idsList) {
                historyManager.remove(id);
            }

            historyManager.remove(epic.getId());
        }
        epicTasks.clear();
        subTasks.clear();
    }

    /**
     * Удаляет все подзадачи.
     */
    @Override
    public void deleteAllSubTasks() {
        for (Epic epicTask : epicTasks.values()) {
            ArrayList<Integer> idsList = epicTask.getSubTaskIds();

            for (Integer id : idsList) {
                historyManager.remove(id);
            }

            idsList.clear();
            epicTask.setStatus(TaskStatus.NEW);
        }

        subTasks.clear();
    }

    /**
     * Удаляет задачу по id.
     */
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача не найдена!");
        }
    }

    /**
     * Удаляет эпик по id.
     */
    @Override
    public void deleteEpicTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Integer> subTasksIds = epicTasks.get(id).getSubTaskIds();

            for (Integer subTaskId : subTasksIds) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }

            epicTasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик не найден!");
        }
    }

    /**
     * Удаляет подзадачу по id.
     */
    @Override
    public void deleteSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            Epic epicTask = epicTasks.get(subTask.getEpicTaskId());

            if (epicTask != null) {
                epicTask.getSubTaskIds().remove(id);
                subTasks.remove(id);
                historyManager.remove(id);
                updateEpicTaskStatus(epicTask);
            } else {
                System.out.println("Не найден эпик подзадачи!");
            }
        } else {
            System.out.println("Подзадача не найдена!");
        }
    }

    /**
     * Добавляет задачи в историю простотра по id.
     */
    @Override
    public void addHistory(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        } else if (epicTasks.containsKey(id)) {
            historyManager.add(epicTasks.get(id));
        } else if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
        }
    }

    /**
     * Получает историю просмотров задач.
     */
    @Override
    public List<Task> getHistory() {
        List<Task> history = historyManager.getHistory();

        if (history.isEmpty()) {
            System.out.print("История просмотров пуста.");
        }

        return history;
    };

    /**
     * Получает инстанцию класса HistoryManager.
     */
    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
