package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;

public class SubTask extends Task{
    private int epicTaskId;

    public SubTask(String name, TaskStatus status, String description, int epicTaskId) {
        super(name, TaskType.SUBTASK, status, description);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(int id, String name, TaskStatus status, String description, int epicTaskId) {
        super(id, name, TaskType.SUBTASK, status, description);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
