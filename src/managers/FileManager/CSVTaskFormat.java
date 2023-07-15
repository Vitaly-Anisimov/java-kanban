package managers.FileManager;

import managers.historyManager.HistoryManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    public static final DateTimeFormatter PATTERN_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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

        TaskType taskType = TaskType.valueOfString(lineValues[1]);
        Status status = Status.valueOfString(lineValues[3]);
        switch (taskType) {
            case TASK:
                return new Task(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]
                        , status
                        , LocalDateTime.parse(lineValues[5], PATTERN_DATE_TIME)
                        , Duration.ofMinutes(Long.parseLong(lineValues[6])));
            case EPIC:
                return new Epic(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]);
            case SUBTASK:
                return new SubTask(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]
                        , status
                        , Integer.parseInt(lineValues[5])
                        , LocalDateTime.parse(lineValues[6], PATTERN_DATE_TIME)
                        , Duration.ofMinutes(Long.parseLong(lineValues[7])));
            default:
                return null;
        }
    }

    public static String toString(final Task task) {
        return task.toString();
    }
}
