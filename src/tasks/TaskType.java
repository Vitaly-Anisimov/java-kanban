package tasks;

public enum TaskType {
    TASK("TASK"),
    EPIC("EPIC"),
    SUBTASK("SUBTASK"),
    UNDEFINED("");
    private String taskChoose;

    TaskType(String taskChoose) {
        this.taskChoose = taskChoose;
    }

    public static TaskType valueOfString(final String value) {
        for (TaskType taskType : values()) {
            if (taskType.taskChoose.equals(value.toUpperCase())) {
                return taskType;
            }
        }
        return UNDEFINED;
    }
}
