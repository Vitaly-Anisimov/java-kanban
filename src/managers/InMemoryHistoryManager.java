package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;


public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_COUNT = 10;
    private final LinkedList<Task> taskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (!(task == null)) {
            taskHistory.add(task);
            if (taskHistory.size() > HISTORY_COUNT) {
                taskHistory.removeFirst();
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
