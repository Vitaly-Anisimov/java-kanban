package tasks;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> epicSubTask;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        super.setTaskType(TaskType.EPIC);

        epicSubTask = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        super.setTaskType(TaskType.EPIC);

        epicSubTask = new ArrayList<>();
    }

    public List<Integer> getIdSubTask() {
        return epicSubTask;
    }

    public void setEpicSubtasks(List<Integer> epicSubTask) {
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
        return getId() + "," +
                TaskType.EPIC + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription();
    }
}