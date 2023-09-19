package manager.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.client.adapters.*;
import manager.file.FileBackedTasksManager;
import manager.history.HistoryManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {
    private final String key;
    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String url, String key) {
        super(new File("test\\savedTasks\\HTTPsave.csv"));

        this.key = key;
        this.kvTaskClient = new KVTaskClient(url);
    }

    public void save() {
        Gson gson = GsonFormatBuilder.buildGson();
        String managerToJson = gson.toJson(this);
        kvTaskClient.put(key, managerToJson);
    }

    public static HttpTaskManager load(String url, String key) {
        Gson gson = GsonFormatBuilder.buildGson();
        KVTaskClient client = new KVTaskClient(url);
        String json = client.load(key);

        if (json.isEmpty()) {
            return new HttpTaskManager(url, key);
        }

        return gson.fromJson(json, HttpTaskManager.class);
    }
}
