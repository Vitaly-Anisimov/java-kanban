package manager.client.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

public class FileGsonAdapter extends TypeAdapter<File> {
    @Override
    public void write(JsonWriter jsonWriter, File file) throws IOException {
        jsonWriter.value(file.getPath());
    }

    @Override
    public File read(final JsonReader jsonReader) throws IOException {
        return new File(jsonReader.nextString());
    }
}
