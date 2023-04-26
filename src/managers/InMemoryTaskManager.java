package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
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
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTask() {
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
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        for (Integer subTaskId : epics.get(id).getIdSubTask()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public void clearEpics() {
        subTasks.clear();
        epics.clear();
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
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++id);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getId());
        changeStatusEpic(epic);
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
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
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.deleteSubTaskId(id);
        subTasks.remove(id);
        changeStatusEpic(epic);
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubTask().clear();
            changeStatusEpic(epic);
        }
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}