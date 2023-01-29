package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;
import ru.yandex.practicum.common.TaskType;

import java.time.Instant;
import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, TaskType.EPIC, TaskStatus.NEW, description, 0L, null);
    }

    public Epic(int id, String name, String description, long duration, Instant startTime, ArrayList<Integer> ids) {
        super(id, name, TaskType.EPIC, TaskStatus.NEW, description, duration, startTime);
        subTaskIds.addAll(ids);
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    public void setSubTaskIds(ArrayList<Integer> ids) {
        subTaskIds.clear();
        subTaskIds.addAll(ids);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
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
