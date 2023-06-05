package managers;

import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import exceptions.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static final String DIRECTORY = "src/savedTasks/";
    public static final String HEAD_FILE = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Task task : historyManager.getHistory()) {
            stringBuilder.append(task.getId() + ",");
        }
        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(final String value) {
        final List<Integer> list = new ArrayList<>();
        final String[] lineValues = value.split(",");

        for (String lineValue : lineValues) {
            list.add(Integer.parseInt(lineValue));
        }
        return list;
    }

    private String toString(final Task task) {
        return task.toString();
    }

    private static Task fromString(final String value) {
        final String[] lineValues = value.split(",");

        TaskType taskType = TaskType.valueOfString(lineValues[1]);
        Status status = Status.valueOfString(lineValues[3]);
        switch (taskType) {
            case TASK:
                return new Task(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]
                        , status);
            case EPIC:
                return new Epic(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]
                        , status);
            case SUBTASK:
                return new SubTask(Integer.parseInt(lineValues[0])
                        , lineValues[2]
                        , lineValues[4]
                        , status
                        , Integer.parseInt(lineValues[5]));
            default:
                return null;
        }
    }

    public static FileBackedTasksManager load(final File file) {
        final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try {
            final List<String> allLinesFile = Files.readAllLines(file.toPath());

            for (int i = 1; i < allLinesFile.size(); i++) {
                if (allLinesFile.get(i).isBlank() || allLinesFile.get(i).isEmpty()) {
                    continue;
                } else if (i == allLinesFile.size() - 1) {
                    List<Integer> history = historyFromString(allLinesFile.get(i));

                    for (Integer taskId : history) {
                        fileBackedTasksManager.getTask(taskId);
                        fileBackedTasksManager.getEpic(taskId);
                        fileBackedTasksManager.getSubTask(taskId);
                    }
                    continue;
                }

                Task task = fromString(allLinesFile.get(i));

                if (task == null) {
                    continue;
                }

                switch (task.getTaskType()) {
                    case TASK:
                        fileBackedTasksManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTasksManager.subTasks.put(task.getId(), (SubTask) task);
                        if (fileBackedTasksManager.epics.containsKey(((SubTask) task).getEpicId())) {
                            Epic epic = fileBackedTasksManager.epics.get(((SubTask) task).getEpicId());
                            epic.addSubTaskId(task.getId());
                        }
                        break;
                }
            }
        } catch (IOException ioe) {
            throw new ManagerLoadException(ioe.getMessage(), ioe.getCause());
        }
        return fileBackedTasksManager;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(HEAD_FILE + "\n");

            for (Task task : getAllTask()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpic()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTask()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }
            bufferedWriter.write("\n" + historyToString(super.historyManager));
        } catch (IOException ioe) {
            throw new ManagerSaveException(ioe.getMessage(), ioe.getCause());
        }
    }

    public static void main(String[] args) {
        File saveFile = new File(DIRECTORY + "SaveFile.csv");
        FileBackedTasksManager fbManagerSave = new FileBackedTasksManager(saveFile);

        Task task1 = new Task("Действие первое", "Пойти в магазин", Status.NEW);
        Task task2 = new Task("Действие второе", "Купить иранскую колу", Status.IN_PROGRESS);
        Task task3 = new Task("Действие третье", "Купить шаурму", Status.DONE);
        fbManagerSave.addTask(task1);
        fbManagerSave.addTask(task2);
        fbManagerSave.addTask(task3);
        Epic epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");
        Epic epic2 = new Epic("Разгадать смысл жизни", "Подумать зачем всё это надо");
        fbManagerSave.addEpic(epic1);
        fbManagerSave.addEpic(epic2);
        SubTask subTask1 = new SubTask("Сделать испанскую защиту", "Выдвинуть 3 пешки и 1 коня", Status.DONE, epic1.getId());
        SubTask subTask2 = new SubTask("Перевести игру в эндшпиль", "Вытащить на середину ферзя", Status.DONE, epic1.getId());
        SubTask subTask3 = new SubTask("Проиграть партию", "Предложить сдаться", Status.IN_PROGRESS, epic1.getId());
        fbManagerSave.addSubTask(subTask1);
        fbManagerSave.addSubTask(subTask2);
        fbManagerSave.addSubTask(subTask3);

        fbManagerSave.getTask(task1.getId());
        fbManagerSave.getEpic(epic2.getId());
        fbManagerSave.getTask(task3.getId());
        fbManagerSave.getSubTask(subTask2.getId());
        fbManagerSave.getSubTask(subTask1.getId());
        fbManagerSave.getEpic(epic1.getId());
        fbManagerSave.getTask(task2.getId());
        fbManagerSave.getSubTask(subTask3.getId());

        File loadFile = new File(DIRECTORY + "SaveFile.csv");
        FileBackedTasksManager fbManagerLoad = load(loadFile);

        System.out.println("Сохраненные таски : ");
        for (Task task : fbManagerLoad.getAllTask()) {
            System.out.println(task.toString());
        }
        System.out.println("Сохраненные эпики : ");
        for (Epic epic : fbManagerLoad.getAllEpic()) {
            System.out.println(epic.toString());
        }
        System.out.println("Сохраненные сабтаски : ");
        for (SubTask subTask : fbManagerLoad.getAllSubTask()) {
            System.out.println(subTask.toString());
        }
        System.out.println("Вывод истории просмотров :");
        for (Task task : fbManagerLoad.getHistory()) {
            System.out.println(task.toString());
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