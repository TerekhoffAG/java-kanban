package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;

import java.time.Instant;
import java.util.Objects;

public class Task {
    static final int SECONDS_IN_MINUTE = 60;
    protected int id;
    protected String name;
    protected TaskType type;
    protected TaskStatus status;
    protected String description;
    protected long duration;
    protected Instant startTime;

    public Task(String name, TaskType type, TaskStatus status, String description, long duration, Instant startTime) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, TaskType type, TaskStatus status, String description, long duration, Instant startTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
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

    public void setDescription(String description) {
        this.description = description;
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

    public long getDuration() {
        return duration;
    }

    public Instant  getStartTime() {
        return startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        long durationInSeconds = duration * SECONDS_IN_MINUTE;
        return startTime != null ? startTime.plusSeconds(durationInSeconds) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) && type == task.type && status == task.status && Objects.equals(description, task.description) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, status, description, duration, startTime);
    }
}
