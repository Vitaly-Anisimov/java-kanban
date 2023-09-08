package manager.file;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import manager.TaskManagerTest;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final File TEST_FILE = new File("test\\savedTasks\\SaveFile.csv");
    @Override
    public FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(TEST_FILE);
    }

    @Test
    public void loadEmptyFile() {
        FileBackedTasksManager emptyTaskManager = FileBackedTasksManager.loadFromFile(new File("test\\savedTasks\\EmptyFile.csv"));

        assertTrue(emptyTaskManager.getAllEpic().isEmpty());
        assertTrue(emptyTaskManager.getAllTask().isEmpty());
        assertTrue(emptyTaskManager.getAllSubTask().isEmpty());
        assertTrue(emptyTaskManager.getPrioritatedTasks().isEmpty());
        assertTrue(emptyTaskManager.getHistory().isEmpty());
    }

    @Test
    public void testLoadSaveWhithoutSubstask() {
        manager.clearSubTasks();
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());

        FileBackedTasksManager testFileManager = FileBackedTasksManager.loadFromFile(TEST_FILE);

        assertEquals(testFileManager.getAllTask().size(), manager.getAllTask().size());
        assertEquals(testFileManager.getAllEpic().size(), manager.getAllEpic().size());
        assertEquals(testFileManager.getAllSubTask().size(), manager.getAllSubTask().size());
        assertEquals(testFileManager.getHistory().size(), manager.getHistory().size());
    }

    @Test
    public void testLoadSaveWithoutSubstaskAndHistory() {
        manager.clearSubTasks();

        FileBackedTasksManager testFileManager = FileBackedTasksManager.loadFromFile(TEST_FILE);

        assertEquals(testFileManager.getAllTask().size(), manager.getAllTask().size());
        assertEquals(testFileManager.getAllEpic().size(), manager.getAllEpic().size());
        assertEquals(testFileManager.getAllSubTask().size(), manager.getAllSubTask().size());
        assertEquals(testFileManager.getHistory().size(), manager.getHistory().size());
    }

    @Test
    public void testLoadSaveWithSubstaskAndHistory() {
        manager.clearTasks();
        manager.clearEpics();
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask1.getId());

        FileBackedTasksManager testFileManager = FileBackedTasksManager.loadFromFile(TEST_FILE);

        assertEquals(testFileManager.getAllTask().size(), manager.getAllTask().size());
        assertEquals(testFileManager.getAllEpic().size(), manager.getAllEpic().size());
        assertEquals(testFileManager.getAllSubTask().size(), manager.getAllSubTask().size());
        assertEquals(testFileManager.getHistory().size(), manager.getHistory().size());
    }

    @Test
    public void testLoadSaveWithEpicAndHistoryWithoutSubtask() {
        manager.clearTasks();
        manager.clearSubTasks();
        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());

        FileBackedTasksManager testFileManager = FileBackedTasksManager.loadFromFile(TEST_FILE);

        assertEquals(testFileManager.getAllTask().size(), manager.getAllTask().size());
        assertEquals(testFileManager.getAllEpic().size(), manager.getAllEpic().size());
        assertEquals(testFileManager.getAllSubTask().size(), manager.getAllSubTask().size());
        assertEquals(testFileManager.getHistory().size(), manager.getHistory().size());
    }

    @Test
    public void testNormalLoadAndSave() {
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getSubTask(subTask1.getId());
        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask3.getId());

        FileBackedTasksManager testFileManager = FileBackedTasksManager.loadFromFile(TEST_FILE);

        assertEquals(testFileManager.getAllTask().size(), manager.getAllTask().size());
        assertEquals(testFileManager.getAllEpic().size(), manager.getAllEpic().size());
        assertEquals(testFileManager.getAllSubTask().size(), manager.getAllSubTask().size());
        assertEquals(testFileManager.getHistory().size(), manager.getHistory().size());
    }


    @Test
    public void testGetLoadException() {
        assertThrows(ManagerLoadException.class, () -> FileBackedTasksManager.loadFromFile(new File("test\\savedTasks\\xxx.csv")));
    }

    @Test
    public void testGetSaveException() {
        FileBackedTasksManager testFileBackManager = new FileBackedTasksManager(new File(":x:asda"));
        assertThrows(ManagerSaveException.class, () -> testFileBackManager.addTask(task1));
    }
}