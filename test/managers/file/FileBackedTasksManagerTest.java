package managers.file;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import managers.mem.TaskManager;
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
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    public void createFileBackedTasksManager() {
        file = new File("test\\savedTasks\\SaveFile.csv");
        fileBackedTasksManager = new FileBackedTasksManager(file);
    }

    @Test
    public void loadEmptyFile() {
        File emptyFile = new File("test\\savedTasks\\EmptyFile.csv");

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

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(file);

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

        FileBackedTasksManager testFileManager = new FileBackedTasksManager(file);

        testFileManager.load();
        assertFalse(testFileManager.getAllEpic().isEmpty());
        assertFalse(testFileManager.getAllTask().isEmpty());
        assertFalse(testFileManager.getAllSubTask().isEmpty());
        assertTrue(testFileManager.getHistory().isEmpty());
    }

    @Test
    public void testGetLoadException() {
        File newFile = new File("xXx");
        FileBackedTasksManager testFileManager = new FileBackedTasksManager(newFile);
        Exception exception = assertThrows(ManagerLoadException.class, () -> {testFileManager.load();});
        assertEquals(exception.getClass(), ManagerLoadException.class);
    }
    @Test
    public void testAddTask() {
        Task newTask = new Task("Действие девятое", "Погладить кота"
                , Status.NEW, LocalDateTime.of(2023, 8, 5, 9, 10)
                , Duration.ofMinutes(30));

        fileBackedTasksManager.addTask(newTask);
        assertEquals(fileBackedTasksManager.getTask(newTask.getId()), newTask);
    }

    @Test
    public void testUpdateTask() {
        task1 = new Task("Действие девятое", "Погладить кота"
                , Status.NEW, LocalDateTime.of(2023, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие десятое", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2011, 8, 5, 10, 40)
                , Duration.ofMinutes(5));

        fileBackedTasksManager.addTask(task2);
        task1.setId(task2.getId());
        fileBackedTasksManager.updateTask(task1);
        assertEquals(fileBackedTasksManager.getTask(task2.getId()), task1);
    }

    @Test
    public void testDeleteTask() {
        task1 = new Task("Действие девятое", "Погладить кота"
                , Status.NEW, LocalDateTime.of(2023, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие десятое", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2011, 8, 5, 10, 40)
                , Duration.ofMinutes(5));

        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.deleteTask(task2.getId());
        fileBackedTasksManager.deleteTask(task1.getId());
        assertTrue(fileBackedTasksManager.getAllTask().isEmpty());
    }

    @Test
    public void testDeleteAllTask() {
        task1 = new Task("Действие девятое", "Погладить кота"
                , Status.NEW, LocalDateTime.of(2023, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие десятое", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2011, 8, 5, 10, 40)
                , Duration.ofMinutes(5));

        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.clearTasks();
        assertTrue(fileBackedTasksManager.getAllTask().isEmpty());
    }

    @Test
    public void testAddEpic() {
        assertTrue(fileBackedTasksManager.getAllEpic().isEmpty());
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);
        assertFalse(fileBackedTasksManager.getAllEpic().isEmpty());
    }

    @Test
    public void testUpdateEpic() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        epic2.setId(epic1.getId());
        fileBackedTasksManager.updateEpic(epic2);
        assertEquals(fileBackedTasksManager.getEpic(epic1.getId()), epic2);
    }

    @Test
    public void testDeleteEpicWithoutSubtask() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);
        assertFalse(fileBackedTasksManager.getAllEpic().isEmpty());
        fileBackedTasksManager.deleteEpic(epic1.getId());
        assertNull(fileBackedTasksManager.getEpic(epic1.getId()));
    }

    @Test
    public void testDeleteEpic() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);

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
        epic1.addSubTaskId(subTask1.getId());
        epic1.addSubTaskId(subTask2.getId());
        assertFalse(fileBackedTasksManager.getAllEpic().isEmpty());
        assertNotNull(fileBackedTasksManager.getSubTask(subTask1.getId()));
        assertNotNull(fileBackedTasksManager.getSubTask(subTask1.getId()));
        fileBackedTasksManager.deleteEpic(epic1.getId());
        assertNull(fileBackedTasksManager.getEpic(epic1.getId()));
        assertTrue(fileBackedTasksManager.getAllSubTask().isEmpty());
        fileBackedTasksManager.deleteEpic(epic2.getId());
        assertTrue(fileBackedTasksManager.getAllEpic().isEmpty());
    }

    @Test
    public void testDeleteAllEpics() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);

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
        epic1.addSubTaskId(subTask1.getId());
        epic1.addSubTaskId(subTask2.getId());
        assertFalse(fileBackedTasksManager.getAllEpic().isEmpty());
        fileBackedTasksManager.clearEpics();
        assertTrue(fileBackedTasksManager.getAllEpic().isEmpty());
    }

    @Test
    public void testAddSubtask() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");
        epic2 = new Epic("Эпик2", "Тестовый эпик");

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);

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
        epic1.addSubTaskId(subTask1.getId());
        epic1.addSubTaskId(subTask2.getId());

        assertEquals(fileBackedTasksManager.getSubTask(subTask1.getId()), subTask1);
        assertEquals(fileBackedTasksManager.getSubTask(subTask2.getId()), subTask2);
    }

    @Test
    public void testUpdateSubtask() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");

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
        subTask2.setId(subTask1.getId());
        fileBackedTasksManager.updateSubTask(subTask2);
        assertEquals(fileBackedTasksManager.getSubTask(subTask1.getId()), subTask2);
    }

    @Test
    public void testDeleteSubtask() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");

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
        assertFalse(fileBackedTasksManager.getAllSubTask().isEmpty());
        fileBackedTasksManager.deleteSubTask(subTask2.getId());
        assertNotNull(fileBackedTasksManager.getSubTask(subTask1.getId()));
        assertNull(fileBackedTasksManager.getSubTask(subTask2.getId()));

        boolean isFoundedId = false;

        for (Integer subTaskId : epic1.getIdSubTask()) {
            if (subTaskId == subTask1.getId()) {
                isFoundedId = true;
                break;
            }
        }

        assertTrue(isFoundedId);
    }

    @Test
    public void testDeleteAllSubtask() {
        epic1 = new Epic("Эпик1", "Тестовый эпик");

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
        fileBackedTasksManager.clearSubTasks();
        assertTrue(fileBackedTasksManager.getAllSubTask().isEmpty());
    }
}