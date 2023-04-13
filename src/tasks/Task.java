package tasks;

public class Task {
    private int id;
    private String name;
    private String caption;
    private TaskStatus status;

    public Task(String name, String caption, TaskStatus status) {
        this.name = name;
        this.caption = caption;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCaption() {
        return caption;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", caption='" + caption + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

