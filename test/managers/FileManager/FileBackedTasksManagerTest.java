package managers.FileManager;

import exceptions.ManagerLoadException;
import managers.taskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest <T extends TaskManager> {
    FileBackedTasksManager fileBackedTasksManager;
    File file;
    Task task1;
    Task task2;
    Epic epic1;

    @BeforeEach
    public void createFileBackedTasksManager() {
        file = new File(FileBackedTasksManager.DIRECTORY + "SaveFile.csv");
        fileBackedTasksManager = new FileBackedTasksManager(file);
    }

    @Test
    public void loadEmptyFile() {
        File emptyFile = new File(FileBackedTasksManager.DIRECTORY + "emptyFile.csv");

        try (Writer writer = new FileWriter(emptyFile)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTasksManager emptyTaskManager = new FileBackedTasksManager(emptyFile);

        try {
            emptyTaskManager.load();
        } catch (ManagerLoadException e) {
            e.printStackTrace();
        }
        assertTrue(emptyTaskManager.getAllEpic().isEmpty());
        assertTrue(emptyTaskManager.getAllTask().isEmpty());
        assertTrue(emptyTaskManager.getAllSubTask().isEmpty());
        assertTrue(emptyTaskManager.getPrioritatedTasks().isEmpty());
        assertTrue(emptyTaskManager.getHistory().isEmpty());
    }

    @Test
    public void testLoadSaveWhithoutSubstask() {
        task1 = new Task("Действие первое", "Пойти в магазин"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие второе", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 40)
                , Duration.ofMinutes(5));
        epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.save();
        fileBackedTasksManager.getTask(task1.getId());
        fileBackedTasksManager.getTask(task2.getId());
        fileBackedTasksManager.getEpic(epic1.getId());

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(file);

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertTrue(testFileManager.getAllSubTask().isEmpty());
        assertFalse(testFileManager.getHistory().isEmpty());
    }

    @Test
    public void testLoadSaveWhithoutSubstaskAndHistory() {
        task1 = new Task("Действие первое", "Пойти в магазин"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие второе", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 40)
                , Duration.ofMinutes(5));
        epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.save();

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(file);

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertTrue(testFileManager.getAllSubTask().isEmpty());
        assertTrue(testFileManager.getHistory().isEmpty());
    }

}