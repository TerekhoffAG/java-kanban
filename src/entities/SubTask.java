package entities;

import common.TaskStatus;

public class SubTask extends Task{
    private int epicTaskId;

    public SubTask(String name, TaskStatus status, String description, int epicTaskId) {
        super(name, status, description);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(int id, String name, TaskStatus status, String description, int epicTaskId) {
        super(id, name, status, description);
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
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
