package manager;

import manager.client.HttpTaskManager;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import server.KVServer;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT, "testkey");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
