package manager.mem;

import exception.ManagerOverlapTimeException;
import manager.Managers;
import manager.TaskManager;
import manager.history.HistoryManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;
    protected final Set<Task> prioritatedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private void checkOverlapTimeTask(Task task) {
        for (Task prioritateTask : prioritatedTasks) {
            if (task.getId() == prioritateTask.getId()) {
                continue;
            }
            if (task.getEndTime().isAfter(prioritateTask.getStartTime())
                && task.getStartTime().isBefore(prioritateTask.getEndTime())) {
                throw new ManagerOverlapTimeException("Произошло наложение по времени между задачами id = " + task.getId() + " и id = " + prioritateTask.getId());
            }
        }
    }

    public List<Task> getPrioritatedTasks() {
        return new ArrayList<>(prioritatedTasks);
    }

    @Override
    public void addTask(Task task) {

        checkOverlapTimeTask(task);
        task.setId(++id);
        tasks.put(task.getId(), task);
        prioritatedTasks.add(task);
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
            prioritatedTasks.remove(task);
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritatedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());

        if (oldTask == null) {
            return;
        }
        checkOverlapTimeTask(task);
        prioritatedTasks.remove(oldTask);
        tasks.put(task.getId(), task);
        prioritatedTasks.add(task);
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    protected void updateEpicDuration(Epic epic) {
        if (epic.getIdSubTasks().isEmpty()) {
            return;
        }

        LocalDateTime tmpStartDate = LocalDateTime.MAX;
        LocalDateTime tmpEndDate = LocalDateTime.MIN;

        for (Integer subTaskid : epic.getIdSubTasks()) {
            SubTask subTask = subTasks.get(subTaskid);

            if (subTask.getStartTime().isBefore(tmpStartDate)) {
                tmpStartDate = subTask.getStartTime();
            }

            if (subTask.getEndTime().isAfter(tmpEndDate)) {
                tmpEndDate = subTask.getEndTime();

            }
        }

        epic.setStartTime(tmpStartDate);
        epic.setDuration(Duration.between(tmpStartDate, tmpEndDate));
    }

    protected void updateEpicStatus(Epic epic) {
        List<Integer> subs = epic.getIdSubTasks();

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
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        updateEpicDuration(epic);
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
            for (Integer subTask : epic.getIdSubTasks()) {
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
        clearSubTasksWithoutUpdateStatusEpic();
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());

        oldEpic.setDescription(epic.getDescription());
        oldEpic.setName(epic.getName());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void addSubTask(SubTask subTask) {
        checkOverlapTimeTask(subTask);

        Epic epic = epics.get(subTask.getEpicId().intValue());

        subTask.setId(++id);
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask.getId());
        updateEpicStatus(epic);
        updateEpicDuration(epic);
        prioritatedTasks.add(subTask);
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
        SubTask oldSubTask = subTasks.get(subTask.getId());

        if (oldSubTask == null) {
            return;
        }
        checkOverlapTimeTask(subTask);
        prioritatedTasks.remove(subTask);
        subTasks.put(subTask.getId(), subTask);
        prioritatedTasks.add(subTask);

        Epic epic = epics.get(subTask.getEpicId().intValue());
        updateEpicDuration(epic);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);

        if (subTask != null) {
            historyManager.remove(id);
            prioritatedTasks.remove(subTask);

            Epic epic = epics.get(subTask.getEpicId());

            if (epic != null) {
                epic.deleteSubTaskId(id);
                updateEpicStatus(epic);
                updateEpicDuration(epic);
            }
        }
    }

    private void clearSubTasksWithoutUpdateStatusEpic() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritatedTasks.remove(subTask);
        }
        subTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        clearSubTasksWithoutUpdateStatusEpic();
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubTasks().clear();
            updateEpicStatus(epic);
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