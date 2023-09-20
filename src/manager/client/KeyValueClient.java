package manager.client;

public interface KeyValueClient {
    void put(String key, String json);

    String load(String key);
}
