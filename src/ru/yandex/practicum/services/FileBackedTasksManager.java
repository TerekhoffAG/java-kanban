package ru.yandex.practicum.services;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;
import ru.yandex.practicum.entities.Epic;
import ru.yandex.practicum.entities.SubTask;
import ru.yandex.practicum.entities.Task;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.interfaces.HistoryManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{
    private File file = new File("resources/task.csv");
    private static final String FILE_HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTasksManager() {
        super();
    }

    /**
     * Представляет задачу в виде строки.
     */
    private String toString(Task task) {
        String line = String.join(
                ",",
                Integer.toString(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription()
        );

        return line + getEpicIdBySubTask(task) + "\n";
    }

    /**
     * Возвращает id эпика для подзадачи.
     */
    private String getEpicIdBySubTask(Task task) {
        return task.getType() == TaskType.SUBTASK
                ? "," + Integer.toString(((SubTask) task).getEpicTaskId())
                : ",";
    }

    /**
     * Представляет строку задачи в виде сущности.
     */
    private Task fromString(String value) {
        Task result;
        String[] properties = value.split(",");
        int id = Integer.parseInt(properties[0]);
        TaskType type = TaskType.valueOf(properties[1]);
        String name = properties[2];
        TaskStatus status = TaskStatus.valueOf(properties[3]);
        String description = properties[4];
        setGeneratedId(id);

        switch (type) {
            case TASK:
                result = new Task(id, name, type, status, description);
                break;
            case EPIC:
                result = new Epic(name, description);
                result.setId(id);
                result.setStatus(status);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(properties[5]);
                result = new SubTask(id, name, status, description, epicId);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return result;
    }

    /**
     * Сохраняет состояние менеджера задач в фаил.
     */
    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write(FILE_HEADER);

            ArrayList<Task> taskList = getAllTask();
            if (!taskList.isEmpty()) {
                for (Task task : taskList) {
                    fileWriter.write(toString(task));
                }
            }

            ArrayList<Epic> epicList = getAllEpicTask();
            if (!epicList.isEmpty()) {
                for (Epic epic : epicList) {
                    fileWriter.write(toString(epic));
                }

                ArrayList<SubTask> subTaskList = getAllSubTask();
                if (!subTaskList.isEmpty()) {
                    for (SubTask subTask : subTaskList) {
                        fileWriter.write(toString(subTask));
                    }
                }
            }

            fileWriter.write("\n");
            fileWriter.write(historyToString(getHistoryManager()));
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    @Override
    public int createTask(Task task) {
        int newId = super.createTask(task);
        save();

        return newId;
    }

    @Override
    public int createEpicTask(Epic epicTask) {
        int newId = super.createEpicTask(epicTask);
        save();

        return newId;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int newId = super.createSubTask(subTask);
        save();

        return newId;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();

        return task;
    }

    @Override
    public Epic getEpicTaskById(int id) {
        Epic epicTask = super.getEpicTaskById(id);
        save();

        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();

        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(Epic epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    /**
     * Представляет историю в виде строки.
     */
    static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();

        if (historyList.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (Task task : historyList) {
            builder.append(task.getId()).append(",");
        }

        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    /**
     * Представляет строку истории в виде списка.
     */
    static List<Integer> historyFromString(String value) {
        List<Integer> idList = new ArrayList<>();
        if (value != null) {
            String[] ids = value.split(",");

            for (String id : ids) {
                idList.add(Integer.parseInt(id));
            }

            return idList;
        }

        return idList;
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }

                Task task = fileBackedTasksManager.fromString(line);

                if (task instanceof Epic epic) {
                    fileBackedTasksManager.addEpic(epic);
                } else if (task instanceof SubTask subTask) {
                    fileBackedTasksManager.addSubTask(subTask);
                } else {
                    fileBackedTasksManager.addTask(task);
                }
            }

            String line = br.readLine();
            if (!line.isEmpty()) {
                for (int id : historyFromString(line)) {
                    fileBackedTasksManager.addHistory(id);
                }
            }

            return fileBackedTasksManager;
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    public static void main(String[] args) {
/////////////////////////////////////////////////////////////////////////////
// TODO: Раскоментировать при загрузке данных из файла ./resources/task.csv
/////////////////////////////////////////////////////////////////////////////
//        File file = new File("resources/task.csv");
//        FileBackedTasksManager tasksManager = loadFromFile(file);
//
//        System.out.println("#########################");
//        System.out.println("История просмотров задач.");
//        System.out.println(tasksManager.getHistory());
//        System.out.println("#########################");
//
//        System.out.println("Список задач.");
//        System.out.println(tasksManager.getAllTask());
//        System.out.println("Список эпиков.");
//        System.out.println(tasksManager.getAllEpicTask());
//        System.out.println("Список подзадач.");
//        System.out.println(tasksManager.getAllSubTask());
    }
}
