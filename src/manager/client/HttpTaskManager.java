package manager.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.client.adapters.*;
import manager.file.CSVTaskFormat;
import manager.file.FileBackedTasksManager;
import manager.history.HistoryManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) {
        super(new File("test\\savedTasks\\HTTPsave.csv"));

        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        Gson gson = GsonFormatBuilder.buildGson();

        String historyToJson = gson.toJson(CSVTaskFormat.historyToString(this.historyManager));
        kvTaskClient.put("history/", historyToJson);

        String tasksToJson = gson.toJson(this.getAllTask());
        kvTaskClient.put("tasks/", tasksToJson);

        String epicsToJson = gson.toJson(this.getAllEpic());
        kvTaskClient.put("epics/", epicsToJson);

        String subTasksToJson = gson.toJson(this.getAllSubTask());
        kvTaskClient.put("subtasks/", subTasksToJson);
    }

    protected void load() {
        HashMap<Integer, Task> addedTasks = new HashMap<>();

        Gson gson = GsonFormatBuilder.buildGson();
        List<Task> tasksFromJson = gson.fromJson(kvTaskClient.load("tasks/"), new TypeToken<List<Task>>(){}.getType());

        tasksFromJson.forEach(task -> {
            this.prioritatedTasks.add(task);
            this.tasks.put(task.getId(), task);
            addedTasks.put(task.getId(), task);
        });

        List<Epic> epicsFromJson = gson.fromJson(kvTaskClient.load("epics/"), new TypeToken<List<Epic>>(){}.getType());
        epicsFromJson.forEach(epic -> {
            this.epics.put(epic.getId(), epic);
            addedTasks.put(epic.getId(), epic);
        });

        List<SubTask> subTasksFromJson = gson.fromJson(kvTaskClient.load("subtasks/"), new TypeToken<List<SubTask>>(){}.getType());
        subTasksFromJson.forEach(subtask -> {
            this.prioritatedTasks.add(subtask);
            this.subTasks.put(subtask.getId(), subtask);
            addedTasks.put(subtask.getId(), subtask);
        });

        String historyFromJson = gson.fromJson(kvTaskClient.load("history/"), String.class);
        List<Integer> parsedHistory = CSVTaskFormat.historyFromString(historyFromJson);

        parsedHistory.forEach((taskId) -> {
            this.historyManager.add(addedTasks.get(taskId));

        } );
    }
}
