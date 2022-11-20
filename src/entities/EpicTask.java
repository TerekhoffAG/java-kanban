package entities;

import common.TaskStatus;

import java.util.ArrayList;

public class EpicTask extends Task{
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, TaskStatus.NEW, description);
    }

    public EpicTask(int id, String name, String description) {
        super(id, name, TaskStatus.NEW, description);
    }

    public void setSubTaskId(int id) {
        this.subTaskIds.add(id);
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return this.subTaskIds;
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
