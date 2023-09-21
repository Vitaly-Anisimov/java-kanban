package manager.http;

public interface KeyValueClient {
    void put(String key, String json);

    String load(String key);
}
