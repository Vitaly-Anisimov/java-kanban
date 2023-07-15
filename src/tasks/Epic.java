package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> epicSubTask;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, LocalDateTime.MIN, Duration.ZERO);

        epicSubTask = new ArrayList<>();
        endTime = LocalDateTime.MIN;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, LocalDateTime.MIN, Duration.ZERO);

        epicSubTask = new ArrayList<>();
        endTime = LocalDateTime.MIN;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return getId() + "," +
                TaskType.EPIC + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Epic epic = (Epic) o;
        return Objects.equals(epic, this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + epicSubTask.hashCode();
        return result;
    }
}