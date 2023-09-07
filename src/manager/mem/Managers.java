package manager.mem;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

import static manager.file.FileBackedTasksManager.fileBackedTasksManagerWithNewFile;

public class Managers {
    public static TaskManager getDefault() {
        return fileBackedTasksManagerWithNewFile();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
