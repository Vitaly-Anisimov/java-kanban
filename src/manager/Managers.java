package manager;

import manager.http.HttpTaskManager;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.http.KVServer;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
