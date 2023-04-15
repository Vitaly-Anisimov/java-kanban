package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> epicSubTask;

    public Epic(String name, String caption) {
        super(name, caption, TaskStatus.NEW);
        epicSubTask = new ArrayList<>();
    }

    public ArrayList<Integer> getIdSubTask() {
        return epicSubTask;
    }

    public void addSubTaskId(int id) {
        epicSubTask.add(id);
    }

    public void deleteSubTaskId(int id) {
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
