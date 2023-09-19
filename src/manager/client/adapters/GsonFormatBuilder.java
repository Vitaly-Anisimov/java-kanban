package manager.client.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.history.HistoryManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class GsonFormatBuilder {
    public static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(File.class, new FileGsonAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationGsonAdapter());
        gsonBuilder.registerTypeAdapter(HistoryManager.class, new HistoryManagerGsonAdapter());
        return gsonBuilder.create();
    }
}
