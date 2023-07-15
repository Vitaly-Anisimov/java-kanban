package managers.taskManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

abstract class TaskManagerTest <T extends TaskManager> {
    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    public T manager;
    abstract T createTaskManager();

    @BeforeEach
    public void createTestTasks() {
        manager = createTaskManager();
        task1 = new Task("Действие первое", "Пойти в магазин"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие второе", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 40)
                , Duration.ofMinutes(5));
        epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");
        epic2 = new Epic("Разгадать смысл жизни", "Подумать зачем всё это надо");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        subTask1 = new SubTask("Сделать испанскую защиту"
                , "Выдвинуть 3 пешки и 1 коня"
                , Status.NEW
                , epic1.getId()
                , LocalDateTime.of(2020, 10, 21, 12, 1)
                , Duration.ofMinutes(3));
        subTask2 = new SubTask("Перевести игру в эндшпиль"
                , "Вытащить на середину ферзя"
                , Status.DONE
                , epic1.getId()
                , LocalDateTime.of(2020, 10, 21, 12, 5)
                , Duration.ofMinutes(5));
        subTask3 = new SubTask("Проиграть партию"
                , "Предложить сдаться"
                , Status.IN_PROGRESS
                , epic1.getId()
                , LocalDateTime.of(2020, 10, 21, 12, 20)
                , Duration.ofMinutes(20));
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
    }

    //Добавление задач

    //Получение задач по id
    @Test
    public void testGetTask() {
        assertEquals(manager.getTask(1), task1);
        assertNull(manager.getTask(101));
    }

    @Test
    public void testGetEpic() {
        assertEquals(manager.getEpic(4), epic2);
        assertNull(manager.getEpic(999));
    }

    @Test
    public void testGetSubTask() {
        assertEquals(manager.getSubTask(6), subTask2);
        assertNull(manager.getSubTask(999));
    }

    // Получение списка задач
    @Test
    public void testGetTasksList() {
        List<Task> testList = new ArrayList<>(List.of(task1, task2));

        assertEquals(testList, manager.getAllTask());
    }

    @Test
    public void testGetEpicList() {
        List<Epic> testList = new ArrayList<>(List.of(epic1, epic2));

        assertEquals(manager.getAllEpic(), testList);
    }

    @Test
    public void testGetSubTaskList() {
        List<SubTask> testList = new ArrayList<>(List.of(subTask1, subTask2, subTask3));

        assertEquals(manager.getAllSubTask(), testList);
    }

    //Удаление задач по id
    @Test
    public void testDeleteTask() {
        assertEquals(manager.getTask(2), task2);
        manager.deleteTask(2);
        assertNull(manager.getTask(2));
    }

    @Test
    public void testDeleteEpic() {
        assertEquals(manager.getEpic(4), epic2);
        manager.deleteEpic(4);
        assertNull(manager.getEpic(4));
    }

    @Test
    public void testDeleteSubTask() {
        assertEquals(manager.getSubTask(6), subTask2);
        manager.deleteSubTask(6);
        assertNull(manager.getSubTask(6));
    }

    //Удаление списка задач
    @Test
    public void testClearTasks() {
        assertFalse(manager.getAllTask().isEmpty());
        manager.clearTasks();
        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    public void testClearEpics() {
        assertFalse(manager.getAllEpic().isEmpty());
        manager.clearEpics();
        assertTrue(manager.getAllEpic().isEmpty());
    }

    @Test
    public void testClearSubtask() {
        assertFalse(manager.getAllSubTask().isEmpty());
        manager.clearSubTasks();
        assertTrue(manager.getAllSubTask().isEmpty());
    }

    //Проверка изменений статуса эпика
    @Test
    public void testChangeEpicStatusNew() {
        Epic testEpic = new Epic("Тест эпика", "Проверка статуса NEW");

        manager.addEpic(testEpic);
        assertEquals(testEpic.getStatus(), Status.NEW);

        SubTask testSubTask1 = new SubTask("Тестовый сабтаск1"
                 , "Проверка статуса NEW1"
                 , Status.NEW
                 , testEpic.getId()
                 , LocalDateTime.of(2021, 10, 21, 12, 5)
                 , Duration.ofMinutes(5));
        SubTask testSubTask2 = new SubTask("Тестовый сабтаск2"
                , "Проверка статуса NEW2"
                , Status.NEW
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 13, 5)
                , Duration.ofMinutes(5));

        manager.addSubTask(testSubTask1);
        manager.addSubTask(testSubTask2);
        assertEquals(testEpic.getStatus(), Status.NEW);
        manager.deleteSubTask(subTask2.getId());
        manager.deleteSubTask(subTask1.getId());
        assertEquals(testEpic.getStatus(), Status.NEW);
    }

    @Test
    public void testChangeEpicStatusInProgress() {
        Epic testEpic = new Epic("Тест эпика", "Проверка статуса Progress");

        manager.addEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Тестовый сабтаск1"
                , "Проверка статуса Progress1"
                , Status.NEW
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 12, 5)
                , Duration.ofMinutes(5));
        SubTask testSubTask2 = new SubTask("Тестовый сабтаск2"
                , "Проверка статуса Progress2"
                , Status.DONE
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 13, 5)
                , Duration.ofMinutes(5));
        SubTask testSubTask3 = new SubTask("Тестовый сабтаск3"
                , "Проверка статуса Progress3"
                , Status.IN_PROGRESS
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 14, 5)
                , Duration.ofMinutes(5));

        manager.addSubTask(testSubTask1);
        manager.addSubTask(testSubTask2);
        manager.addSubTask(testSubTask3);
        assertEquals(testEpic.getStatus(), Status.IN_PROGRESS);
        manager.deleteSubTask(testSubTask3.getId());
        assertEquals(testEpic.getStatus(), Status.IN_PROGRESS);
    }

    @Test
    public void testChangeEpicStatusDone() {
        Epic testEpic = new Epic("Тест эпика", "Проверка статуса Done");

        manager.addEpic(testEpic);

        SubTask testSubTask1 = new SubTask("Тестовый сабтаск1"
                , "Проверка статуса Done1"
                , Status.DONE
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 12, 5)
                , Duration.ofMinutes(5));
        SubTask testSubTask2 = new SubTask("Тестовый сабтаск2"
                , "Проверка статуса Done2"
                , Status.NEW
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 13, 5)
                , Duration.ofMinutes(5));
        SubTask testSubTask3 = new SubTask("Тестовый сабтаск3"
                , "Проверка статуса Done3"
                , Status.DONE
                , testEpic.getId()
                , LocalDateTime.of(2021, 10, 21, 14, 5)
                , Duration.ofMinutes(5));

        manager.addSubTask(testSubTask1);
        manager.addSubTask(testSubTask2);
        manager.addSubTask(testSubTask3);
        assertEquals(testEpic.getStatus(), Status.IN_PROGRESS);
        manager.deleteSubTask(testSubTask2.getId());
        testSubTask2.setStatus(Status.DONE);
        manager.addSubTask(testSubTask2);
        assertEquals(testEpic.getStatus(), Status.DONE);
    }

    @Test
    public void testgetPrioritatedTasks() {
        Set<Task> testSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        testSet.addAll(manager.getAllTask());
        testSet.addAll(manager.getAllEpic());
        testSet.addAll(manager.getAllSubTask());

        Set<Task> setFromManager = manager.getPrioritatedTasks();

        assertEquals(testSet, setFromManager);
        manager.clearTasks();
        manager.clearSubTasks();
        manager.clearEpics();
        assertTrue(manager.getPrioritatedTasks().isEmpty());
    }
}