package managers.taskManager;

import managers.historyManager.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;
    private final Set<Task> prioritatedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    private void checkOverlapTimeTask(Task task) {
        for (Task prioritateTask : prioritatedTasks) {
            if ((task.getStartTime().isAfter(prioritateTask.getStartTime()) && task.getStartTime().isBefore(prioritateTask.getStartTime()))
                    || (task.getEndTime().isAfter(prioritateTask.getStartTime()) && task.getEndTime().isBefore(prioritateTask.getEndTime()))) {
                throw new RuntimeException("Произошло наложение задач по времени!");
            }
        }
    }

    public Set<Task> getPrioritatedTasks() {
        return prioritatedTasks;
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

    private void calcTimesEpic(Epic epic) {
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

    protected void changeStatusEpic(Epic epic) {
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
        prioritatedTasks.add(epic);
        calcTimesEpic(epic);
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
        clearSubTasks();
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());

        oldEpic.setDescription(epic.getDescription());
        oldEpic.setName(epic.getName());
        changeStatusEpic(oldEpic);
        prioritatedTasks.remove(epic);
        prioritatedTasks.add(epic);
        calcTimesEpic(oldEpic);
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
        changeStatusEpic(epic);
        calcTimesEpic(epic);
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
        changeStatusEpic(epic);
        prioritatedTasks.remove(subTask);
        prioritatedTasks.add(subTask);
        calcTimesEpic(epic);
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
                changeStatusEpic(epic);
                calcTimesEpic(epic);
            }
        }
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritatedTasks.remove(subTask);
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

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}