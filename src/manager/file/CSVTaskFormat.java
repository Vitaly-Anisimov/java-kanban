package manager.file;

import manager.history.HistoryManager;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    public static final DateTimeFormatter PATTERN_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final String HEAD_FILE = "id,type,name,status,description,epic,starttime,duration";

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
        LocalDateTime startTime = LocalDateTime.parse(lineValues[6], PATTERN_DATE_TIME);
        Duration duration = Duration.ofMinutes(Long.parseLong(lineValues[7]));

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
                        , duration);
            case SUBTASK:
                return new SubTask(id
                        , name
                        , description
                        , status
                        , startTime
                        , duration
                        , Integer.parseInt(lineValues[5]));
            default:
                return null;
        }
    }

    public static String toString(final Task task) {
        final TaskType taskType = task.getTaskType();
        String epicid = null;

        if (taskType == TaskType.SUBTASK) {
            SubTask subTask = (SubTask) task;

            epicid = String.valueOf(subTask.getEpicId());
        }

        String taskToString = task.getId() + "," +
                taskType + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                epicid + "," +
                task.getStartTime().format(PATTERN_DATE_TIME) + "," +
                task.getDuration().toMinutes();

        return taskToString;
    }
}
