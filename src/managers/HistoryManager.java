package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}
