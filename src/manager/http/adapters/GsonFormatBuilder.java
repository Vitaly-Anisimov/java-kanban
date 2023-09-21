package manager.http.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonFormatBuilder {
    public static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationGsonAdapter());
        return gsonBuilder.create();
    }
}
