package tasks;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String caption, TaskStatus status, int epicId) {
        super(name, caption, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", epicId=" + epicId + '\'' +
                ", name='" + getName() + '\'' +
                ", caption='" + getCaption() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}

