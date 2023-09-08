package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> epicSubTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, LocalDateTime.MAX, Duration.ZERO);
        setTaskType(TaskType.EPIC);

        epicSubTasks = new ArrayList<>();
    }

    public Epic(int id
            , String name
            , String description
            , Status status
            , LocalDateTime startTime
            , Duration duration) {
        super(id, name, description, status, startTime, duration);
        setTaskType(TaskType.EPIC);

        epicSubTasks = new ArrayList<>();
    }

    public List<Integer> getIdSubTasks() {
        return epicSubTasks;
    }

    public void addSubTaskId(int id) {
        epicSubTasks.add(id);
    }

    public void deleteSubTaskId(Integer id) {
        epicSubTasks.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Epic epic = (Epic) o;
        return super.equals(epic)
                && this.getEndTime().equals(epic.getEndTime())
                && this.getIdSubTasks().equals(epic.getIdSubTasks());
    }
}