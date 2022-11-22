package ru.yandex.practicum.entities;

import ru.yandex.practicum.common.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, TaskStatus.NEW, description);
    }

    public Epic(int id, String name, String description, ArrayList<Integer> ids) {
        super(id, name, TaskStatus.NEW, description);
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
        return "EpicTask{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
