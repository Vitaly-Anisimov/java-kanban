package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> epicSubTask;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        epicSubTask = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        epicSubTask = new ArrayList<>();
    }

    public ArrayList<Integer> getIdSubTask() {
        return epicSubTask;
    }

    public void setEpicSubtasks(ArrayList<Integer> epicSubTask) {
        this.epicSubTask = epicSubTask;
    }

    public void addSubTaskId(int id) {
        epicSubTask.add(id);
    }

    public void deleteSubTaskId(Integer id) {
        epicSubTask.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", epicSubTask=" + epicSubTask +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
