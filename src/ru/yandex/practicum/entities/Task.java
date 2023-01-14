package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;

public class Task {
    protected int id;
    protected String name;
    protected TaskType type;
    protected TaskStatus status;
    protected String description;

    public Task(String name, TaskType type, TaskStatus status, String description) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public Task(int id, String name, TaskType type, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
