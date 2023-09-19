package manager.file;

import manager.mem.InMemoryTaskManager;
import model.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exception.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void load() {
        try {
            final List<String> allLinesFile = Files.readAllLines(file.toPath());
            List<Integer> history = new ArrayList<>();
            HashMap<Integer,Task> addedTask = new HashMap<>();
            boolean isEmptyLine = false;
            boolean isFirstLine = true;

            for (String lineFromFile : allLinesFile) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (lineFromFile.isEmpty() || lineFromFile.isBlank()) {
                    isEmptyLine = true;
                    continue;
                }

                if (isEmptyLine) {
                    history = CSVTaskFormat.historyFromString(lineFromFile);
                    continue;
                }

                Task task = CSVTaskFormat.fromString(lineFromFile);

                switch (task.getTaskType()) {
                    case TASK:
                        super.tasks.put(task.getId(), task);
                        super.prioritatedTasks.add(task);
                        break;
                    case EPIC:
                        super.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        super.subTasks.put(task.getId(), (SubTask) task);
                        super.prioritatedTasks.add(task);
                        break;
                }

                addedTask.put(task.getId(), task);
            }

            for (SubTask subTask : super.getAllSubTask()) {
                Epic subtaskEpic = super.epics.get(subTask.getEpicId());

                if (subtaskEpic == null) {
                    continue;
                }

                subtaskEpic.addSubTaskId(subTask.getId());
            }

            for (Epic epic : super.getAllEpic()) {
                super.updateEpicDuration(epic);
            }

            for (Integer taskIdHistory : history) {
                super.historyManager.add(addedTask.get(taskIdHistory));
            }

            int maxId = 0;

            for (Task task : addedTask.values()) {
                if (maxId < task.getId()) {
                    maxId = task.getId();
                }
            }

            super.id = maxId;


        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage() + " " + file.getName(), e);
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        fileBackedTasksManager.load();
        return fileBackedTasksManager;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(CSVTaskFormat.HEAD_FILE);
            bufferedWriter.newLine();

            for (Task task : getAllTask()) {
                bufferedWriter.write(CSVTaskFormat.toString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : getAllEpic()) {
                bufferedWriter.write(CSVTaskFormat.toString(epic));
                bufferedWriter.newLine();
            }
            for (SubTask subTask : getAllSubTask()) {
                bufferedWriter.write(CSVTaskFormat.toString(subTask));
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
            bufferedWriter.write(CSVTaskFormat.historyToString(super.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage() + " " + file.getName(), e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);

        if (task == null) {
            return null;
        }
        save();
        return task;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);

        if (epic == null) {
            return null;
        }
        save();
        return epic;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);

        if (subTask == null) {
            return null;
        }
        save();
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }
}
