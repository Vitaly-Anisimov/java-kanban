package tasks;

import managers.file.CSVTaskFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        setTaskType(TaskType.SUBTASK);

        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        setTaskType(TaskType.SUBTASK);

        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubTask subTask = (SubTask) o;
        return Objects.equals(subTask, this);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + getEpicId();
        return result;
    }
}

