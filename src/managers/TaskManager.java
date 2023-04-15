package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int id;

    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        for (Integer subTaskId : epic.getIdSubTask()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    public void changeStatusEpic(Epic epic) {
        boolean allDone = true;
        boolean allNew = true;
        if (epic.getIdSubTask().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            for (Integer subTaskId : epic.getIdSubTask()) {
                SubTask subTask = subTasks.get(subTaskId);
                if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                } else if (subTask.getStatus() == TaskStatus.DONE) {
                    allNew = false;
                } else {
                    allDone = false;
                }
            }
            if (allDone && epic.getStatus() != TaskStatus.DONE) {
                epic.setStatus(TaskStatus.DONE);
            } else if (allNew && epic.getStatus() != TaskStatus.NEW) {
                epic.setStatus(TaskStatus.NEW);
            } else if (epic.getStatus() != TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public void addSubTask(SubTask subTask) {
        subTask.setId(++id);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getId());
        changeStatusEpic(epic);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public void updateSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTasks.put(subTask.getId(), subTask);
        changeStatusEpic(epic);
    }

    public void deleteSubTask(int id) {
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.getIdSubTask().remove((Integer) id);
        subTasks.remove(id);
        changeStatusEpic(epic);
    }

    public void clearSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubTask().clear();
            changeStatusEpic(epic);
        }
    }

    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }


}
