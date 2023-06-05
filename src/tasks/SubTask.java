package tasks;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        super.setTaskType(TaskType.SUBTASK);

        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        super.setTaskType(TaskType.SUBTASK);

        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getId() + "," +
                TaskType.SUBTASK + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getEpicId();
    }
}

