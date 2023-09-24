package manager.history;

import manager.Managers;
import manager.TaskManager;
import manager.file.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager;
    Task task1;
    Task task2;
    Epic epic1;
    SubTask subTask1;

    @BeforeEach
    public void addTasksInHistoryManager() {
        taskManager = new FileBackedTasksManager(new File("test\\savedTasks\\SaveFile.csv"));
        task1 = new Task("Действие первое", "Пойти в магазин"
                    , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                    , Duration.ofMinutes(30));
        task2 = new Task("Действие второе", "Купить иранскую колу"
                    , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 40)
                    , Duration.ofMinutes(5));
        epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);

        subTask1 = new SubTask("Сделать испанскую защиту"
                    , "Выдвинуть 3 пешки и 1 коня"
                    , Status.NEW
                    , epic1.getId()
                    , LocalDateTime.of(2020, 10, 21, 12, 1)
                    , Duration.ofMinutes(3));
        taskManager.addSubTask(subTask1);
    }

    @Test
    public void testGetCountHistory() {
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        assertEquals(4, taskManager.getHistory().size());
    }

    @Test
    public void testGetDistinctHistory() {
        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    public void testDeleteFromTopHistory() {
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.deleteSubTask(subTask1.getId());
        assertFalse(taskManager.getHistory().isEmpty());
        assertEquals(taskManager.getHistory().get(0), task1);
    }

    @Test
    public void testDeleteFromMidHistory() {
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.deleteTask(task2.getId());
        assertFalse(taskManager.getHistory().isEmpty());
        assertEquals(taskManager.getHistory().get(2), epic1);
    }

    @Test
    public void testDeleteFromBotHistory() {
        taskManager.getSubTask(subTask1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.deleteEpic(epic1.getId());
        assertFalse(taskManager.getHistory().isEmpty());
        assertFalse(taskManager.getHistory().contains(epic1));
    }

    @Test
    public void testReturnHistory() {
        List<Task> history = taskManager.getHistory();

        assertTrue(history.isEmpty());
        taskManager.getEpic(epic1.getId());
        history = taskManager.getHistory();
        assertFalse(history.isEmpty());
    }
}