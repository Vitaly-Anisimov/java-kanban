package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;

    @Override
    public void addTask(Task task) {

        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);

        if (task != null) {
            historyManager.remove(id);
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);

        if (epic != null) {
            for (Integer subTask : epic.getIdSubTask()) {
                deleteSubTask(subTask);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        clearSubTasks();
    }

    private void changeStatusEpic(Epic epic) {
        List<Integer> subs = epic.getIdSubTask();

        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        Status status = null;

        for (int id : subs) {
            final SubTask subtask = subTasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status == subtask.getStatus()
                    && status != Status.IN_PROGRESS) {
                continue;
            }
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        epic.setStatus(status);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());

        oldEpic.setDescription(epic.getDescription());
        oldEpic.setName(epic.getName());
        changeStatusEpic(oldEpic);
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());

        subTask.setId(++id);
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask.getId());
        changeStatusEpic(epic);
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        if (subTask == null) {
            return null;
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());

        subTasks.put(subTask.getId(), subTask);
        changeStatusEpic(epic);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);

        if (subTask != null) {
            historyManager.remove(id);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.deleteSubTaskId(id);
                changeStatusEpic(epic);
            }
        }
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubTask().clear();
            changeStatusEpic(epic);
        }
    }

    @Override
    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}