package managers.FileManager;

import managers.taskManager.InMemoryTaskManager;
import tasks.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import exceptions.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static final String DIRECTORY = "src/savedTasks/";
    private final String HEAD_FILE = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public void load() {
        try {
            final List<String> allLinesFile = Files.readAllLines(file.toPath());

            for (int i = 1; i < allLinesFile.size(); i++) {
                if (allLinesFile.get(i).isBlank() || allLinesFile.get(i).isEmpty()) {
                    continue;
                } else if (i == allLinesFile.size() - 1) {
                    List<Integer> history = CSVTaskFormat.historyFromString(allLinesFile.get(i));

                    for (Integer taskId : history) {
                        super.getTask(taskId);
                        super.getEpic(taskId);
                        super.getSubTask(taskId);
                    }
                    continue;
                }

                Task task = CSVTaskFormat.fromString(allLinesFile.get(i));
                if (task == null) {
                    continue;
                }
                TaskType taskType = TaskType.valueOfString(task.getClass().getSimpleName());

                switch (taskType) {
                    case TASK:
                        super.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        super.epics.put(task.getId(), (Epic) task);
                        super.changeStatusEpic((Epic) task);
                        break;
                    case SUBTASK:
                        super.subTasks.put(task.getId(), (SubTask) task);
                        if (super.epics.containsKey(((SubTask) task).getEpicId())) {
                            Epic epic = super.epics.get(((SubTask) task).getEpicId());
                            epic.addSubTaskId(task.getId());
                            super.changeStatusEpic(epic);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage(), e.getCause());
        }
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(HEAD_FILE + "\n");

            for (Task task : getAllTask()) {
                bufferedWriter.write(CSVTaskFormat.toString(task) + "\n");
            }
            for (Epic epic : getAllEpic()) {
                bufferedWriter.write(CSVTaskFormat.toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTask()) {
                bufferedWriter.write(CSVTaskFormat.toString(subTask) + "\n");
            }
            bufferedWriter.write("\n" + CSVTaskFormat.historyToString(super.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage(), e.getCause());
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

    /*public static void main(String[] args) {
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
        FileBackedTasksManager fbManagerLoad = new FileBackedTasksManager(loadFile);
        fbManagerLoad.load();

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
    }*/
}
