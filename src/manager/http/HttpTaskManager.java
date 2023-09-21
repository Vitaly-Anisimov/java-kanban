package manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.file.CSVTaskFormat;
import manager.file.FileBackedTasksManager;
import manager.http.adapters.GsonFormatBuilder;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    protected final KVTaskClient kvTaskClient;

    public HttpTaskManager(String url) {
        super(null);

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

    public static HttpTaskManager load(String url) {
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);
        HashMap<Integer, Task> addedTasks = new HashMap<>();

        Gson gson = GsonFormatBuilder.buildGson();
        List<Task> tasksFromJson = gson.fromJson(httpTaskManager.kvTaskClient.load("tasks/"), new TypeToken<List<Task>>(){}.getType());

        tasksFromJson.forEach(task -> {
            httpTaskManager.prioritatedTasks.add(task);
            httpTaskManager.tasks.put(task.getId(), task);
            addedTasks.put(task.getId(), task);
        });

        List<Epic> epicsFromJson = gson.fromJson(httpTaskManager.kvTaskClient.load("epics/"), new TypeToken<List<Epic>>(){}.getType());
        epicsFromJson.forEach(epic -> {
            httpTaskManager.epics.put(epic.getId(), epic);
            addedTasks.put(epic.getId(), epic);
        });

        List<SubTask> subTasksFromJson = gson.fromJson(httpTaskManager.kvTaskClient.load("subtasks/"), new TypeToken<List<SubTask>>(){}.getType());
        subTasksFromJson.forEach(subtask -> {
            httpTaskManager.prioritatedTasks.add(subtask);
            httpTaskManager.subTasks.put(subtask.getId(), subtask);
            addedTasks.put(subtask.getId(), subtask);
        });

        String historyFromJson = gson.fromJson(httpTaskManager.kvTaskClient.load("history/"), String.class);
        List<Integer> parsedHistory = CSVTaskFormat.historyFromString(historyFromJson);

        parsedHistory.forEach((taskId) -> {
            httpTaskManager.historyManager.add(addedTasks.get(taskId));

        } );

        return httpTaskManager;
    }
}
