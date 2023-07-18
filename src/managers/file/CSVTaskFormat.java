package managers.file;

import managers.history.HistoryManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    public static final DateTimeFormatter PATTERN_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final String HEAD_FILE = "id,type,name,status,description,epic";

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Task task : historyManager.getHistory()) {
            stringBuilder.append(task.getId() + ",");
        }
        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(final String value) {
        final List<Integer> list = new ArrayList<>();
        final String[] lineValues = value.split(",");

        for (String lineValue : lineValues) {
            list.add(Integer.parseInt(lineValue));
        }
        return list;
    }

    public static Task fromString(final String value) {
        final String[] lineValues = value.split(",");

        int id = Integer.parseInt(lineValues[0]);
        TaskType taskType = TaskType.valueOfString(lineValues[1]);
        String name = lineValues[2];
        Status status = Status.valueOfString(lineValues[3]);
        String description = lineValues[4];
        LocalDateTime startTime = LocalDateTime.parse(lineValues[5], PATTERN_DATE_TIME);
        Duration duration = Duration.ofMinutes(Long.parseLong(lineValues[6]));

        switch (taskType) {
            case TASK:
                return new Task(id
                        , name
                        , description
                        , status
                        , startTime
                        , duration);
            case EPIC:
                return new Epic(id
                        , name
                        , description
                        , status
                        , startTime
                        , duration
                        , LocalDateTime.parse(lineValues[7], PATTERN_DATE_TIME));
            case SUBTASK:
                return new SubTask(id
                        , name
                        , description
                        , status
                        , startTime
                        , duration
                        , Integer.parseInt(lineValues[7]));
            default:
                return null;
        }
    }

    public static String toString(final Task task) {
        final TaskType taskType = task.getTaskType();

        StringBuilder taskToString = new StringBuilder(task.getId() + "," +
                taskType + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getStartTime().format(PATTERN_DATE_TIME) + "," +
                task.getDuration().toMinutes() + ",");

        if (taskType == TaskType.EPIC) {
            Epic epic = (Epic) task;
            taskToString.append(epic.getEndTime().format(PATTERN_DATE_TIME));
        } else if (taskType == TaskType.SUBTASK) {
            SubTask subtask = (SubTask) task;
            taskToString.append(subtask.getEpicId());
        }

        return taskToString.toString();
    }
}
