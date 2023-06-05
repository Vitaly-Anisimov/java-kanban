package tasks;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private TaskType taskType;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.taskType = TaskType.TASK;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getId() + "," +
                TaskType.TASK + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription();
    }
}

