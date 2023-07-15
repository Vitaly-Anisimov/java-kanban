package managers.taskManager;
import managers.historyManager.HistoryManager;
import tasks.*;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    void addTask(Task task);

    Task getTask(int id);

    void deleteTask(int id);

    void clearTasks();

    void updateTask(Task task);

    List<Task> getAllTask();

    void addEpic(Epic epic);

    Epic getEpic(int id);

    void deleteEpic(int id);

    void clearEpics();

    void updateEpic(Epic epic);

    List<Epic> getAllEpic();

    void addSubTask(SubTask subTask);

    SubTask getSubTask(int id);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    void clearSubTasks();

    List<SubTask> getAllSubTask();

    List<Task> getHistory();

    Set<Task> getPrioritatedTasks();

    HistoryManager getHistoryManager();
}
