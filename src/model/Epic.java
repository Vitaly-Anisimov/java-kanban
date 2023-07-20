package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> epicSubTask;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, LocalDateTime.MAX, Duration.ZERO);
        setTaskType(TaskType.EPIC);

        epicSubTask = new ArrayList<>();
        endTime = LocalDateTime.MIN.plusYears(1);
    }

    public Epic(int id
            , String name
            , String description
            , Status status
            , LocalDateTime startTime
            , Duration duration) {
        super(id, name, description, status, startTime, duration);
        setTaskType(TaskType.EPIC);

        epicSubTask = new ArrayList<>();
        this.endTime = LocalDateTime.MIN.plusYears(1);
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