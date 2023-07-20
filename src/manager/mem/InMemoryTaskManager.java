package manager.mem;

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
            if ((task.getStartTime().isAfter(prioritateTask.getStartTime()) && task.getStartTime().isBefore(prioritateTask.getStartTime()))
                    || (task.getEndTime().isAfter(prioritateTask.getStartTime()) && task.getEndTime().isBefore(prioritateTask.getEndTime()))
                    || (task.getStartTime().compareTo(prioritateTask.getStartTime()) == 0 && task.getEndTime().compareTo(prioritateTask.getEndTime()) == 0)) {
                throw new RuntimeException("Произошло наложение задач по времени!");
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
        checkOverlapTimeTask(task);
        tasks.put(task.getId(), task);
        prioritatedTasks.remove(task);
        prioritatedTasks.add(task);
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    protected void updateEpicDuration(Epic epic) {
        if (epic.getIdSubTask().isEmpty()) {
            return;
        }

        SubTask firstSubtask = subTasks.get(epic.getIdSubTask().get(0));
        LocalDateTime epicStartTime = firstSubtask.getStartTime();
        LocalDateTime epicEndTime = firstSubtask.getEndTime();
        Duration epicDuration = Duration.ZERO;

        for (Integer subTaskid : epic.getIdSubTask()) {
            SubTask subTask = subTasks.get(subTaskid);

            epicDuration = epicDuration.plus(subTask.getDuration());

            if (subTask.getStartTime().isBefore(epicStartTime)) {
                epic.setStartTime(subTask.getStartTime());
            }

            if (subTask.getEndTime().isAfter(epicEndTime)) {
                epic.setEndTime(subTask.getEndTime());
            }
        }

        epic.setDuration(epicDuration);
    }

    protected void updateEpicStatus(Epic epic) {
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
            for (Integer subTask : epic.getIdSubTask()) {
                deleteSubTask(subTask);
            }
            prioritatedTasks.remove(epic);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritatedTasks.remove(epic);
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

        Epic epic = epics.get(subTask.getEpicId());

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
        checkOverlapTimeTask(subTask);

        Epic epic = epics.get(subTask.getEpicId());

        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(epic);
        prioritatedTasks.remove(subTask);
        prioritatedTasks.add(subTask);
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
            epic.getIdSubTask().clear();
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