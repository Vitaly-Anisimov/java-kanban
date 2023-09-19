package manager.client.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import manager.history.InMemoryHistoryManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryManagerGsonAdapter extends TypeAdapter<InMemoryHistoryManager> {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonAdapter())
            .registerTypeAdapter(Duration.class, new DurationGsonAdapter())
            .create();

    public void write(JsonWriter writer, InMemoryHistoryManager historyManager) throws IOException {
        writer.beginObject();
        for (Task task : historyManager.getHistory()) {
            writer.name(task.getClass().getName());
            writer.value(gson.toJson(task));
        }
        writer.endObject();
        //System.out.println(writer.toString());
    }

    public InMemoryHistoryManager read(JsonReader reader) throws IOException {
        InMemoryHistoryManager manager;

        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        } else {
            manager = new InMemoryHistoryManager();
            reader.beginObject();
            String field = null;

            while (reader.hasNext()) {
                JsonToken token = reader.peek();
                if (token.equals(JsonToken.NAME)) {
                    field = reader.nextName();
                }
                if (field == null) {
                    reader.peek();
                } else if (field.contains("Epic")) {
                    reader.peek();
                    manager.add(gson.fromJson(reader.nextString(), Epic.class));
                } else if (field.contains("Subtask")) {
                    reader.peek();
                    manager.add(gson.fromJson(reader.nextString(), SubTask.class));
                } else {
                    reader.peek();
                   // Task task = gson.fromJson(reader.nextString(), Task.class);
                   // System.out.println(CSVTaskFormat.toString(task));
                   manager.add(gson.fromJson(reader.nextString(), Task.class));
                }
            }
        }
        reader.endObject();
        return manager;
    }
}
