package manager.file;

import exception.ManagerLoadException;
import manager.mem.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    Task task1;
    Task task2;
    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;

    FileBackedTasksManager fileBackedTasksManager;

    @Override
    public FileBackedTasksManager createTaskManager() {
        return FileBackedTasksManager.fileBackedTasksManagerWithNewFile();
    }

    @BeforeEach
    public void createFileBackedTaskManager() {
        fileBackedTasksManager = createTaskManager();
    }

    @Test
    public void loadEmptyFile() {
        FileBackedTasksManager emptyTaskManager = new FileBackedTasksManager(new File("test\\savedTasks\\EmptyFile.csv"));

        emptyTaskManager.load();
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

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(new File("test\\savedTasks\\SaveFile.csv"));

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertTrue(testFileManager.getAllSubTask().isEmpty());
        assertFalse(testFileManager.getHistory().isEmpty());
    }

    @Test
    public void testLoadSaveWithoutSubstaskAndHistory() {
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

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(new File("test\\savedTasks\\SaveFile.csv"));

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertTrue(testFileManager.getAllSubTask().isEmpty());
        assertTrue(testFileManager.getHistory().isEmpty());
    }

    @Test
    public void testLoadSaveWithSubstaskAndHistory() {
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

        subTask1 = new SubTask("Тестовый сабтаск1"
                , "Проверка статуса Done1"
                , Status.DONE
                , epic1.getId()
                , LocalDateTime.of(2021, 10, 21, 12, 5)
                , Duration.ofMinutes(5));
        subTask2 = new SubTask("Тестовый сабтаск2"
                , "Проверка статуса Done2"
                , Status.NEW
                , epic1.getId()
                , LocalDateTime.of(2021, 10, 21, 13, 5)
                , Duration.ofMinutes(5));

        fileBackedTasksManager.addSubTask(subTask1);
        fileBackedTasksManager.addSubTask(subTask2);
        fileBackedTasksManager.save();

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(new File("test\\savedTasks\\SaveFile.csv"));

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertFalse(testFileManager.getAllSubTask().isEmpty());
        assertTrue(testFileManager.getHistory().isEmpty());
    }

    @Test
    public void testGetLoadException() {
        FileBackedTasksManager testFileManager = new FileBackedTasksManager(new File("test\\savedTasks\\xxx.csv"));

        Exception exception = assertThrows(ManagerLoadException.class, testFileManager::load);
        assertEquals(exception.getClass(), ManagerLoadException.class);
    }
}