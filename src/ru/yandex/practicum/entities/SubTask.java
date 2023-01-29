package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;

import java.time.Instant;
import java.util.Objects;

public class SubTask extends Task{
    private int epicTaskId;

    public SubTask(String name, TaskStatus status, String description, long duration, Instant startTime, int epicTaskId) {
        super(name, TaskType.SUBTASK, status, description, duration, startTime);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(int id, String name, TaskStatus status, String description, long duration, Instant startTime, int epicTaskId) {
        super(id, name, TaskType.SUBTASK, status, description, duration, startTime);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask subTask)) return false;
        if (!super.equals(o)) return false;
        return epicTaskId == subTask.epicTaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskId);
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
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
