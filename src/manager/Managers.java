package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault() {
        //Не понимаю для каких целей вернуть FileBackedManager
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
