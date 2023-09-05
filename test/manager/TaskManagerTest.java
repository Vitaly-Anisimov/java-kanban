package manager;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.ManagerOverlapTimeException;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

abstract class TaskManagerTest <T extends TaskManager> {
    Task task1;
    Task task2;
    Task task3;
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
        task3 = new Task("Действие третье", "тест"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 50)
                , Duration.ofMinutes(10));

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

    @Test
    public void testUpdateTaskWithChanges() {
        Task newTask1 = new Task("Действие первое", "Обновленная таска"
                , Status.NEW, LocalDateTime.of(2011, 8, 5, 9, 10)
                , Duration.ofMinutes(35));

        newTask1.setId(task1.getId());
        manager.updateTask(newTask1);
        Task taskFromManager = manager.getTask(newTask1.getId());
        assertEquals(taskFromManager, newTask1);
        assertTrue(manager.getPrioritatedTasks().contains(newTask1));
    }

    @Test
    public void testUpdateTaskWithOverlapTask() {
        task1.setStartTime(LocalDateTime.of(2010, 8, 5, 10, 40));
        assertThrows(ManagerOverlapTimeException.class, () -> manager.updateTask(task1));
    }

    @Test
    public void testUpdateTaskWithNotOverlapTask() {
        task3.setStartTime(LocalDateTime.of(2010, 8, 5, 10, 55));
        assertDoesNotThrow(() -> manager.updateTask(task2));
    }

    @Test
    public void testUpdateTaskChangeDurationThrowsException() {
        task1.setDuration(Duration.ofMinutes(120));
        assertThrows(ManagerOverlapTimeException.class, () -> manager.updateTask(task1));
    }

    @Test
    public void testUpdateTaskChangeDurationNotThrowsException() {
        task3.setDuration(task3.getDuration().minus(Duration.ofMinutes(5)));
        assertDoesNotThrow(() -> manager.updateTask(task3));
    }

    @Test
    public void testUpdateTaskWithoutChanges() {
        Task newTask1 = new Task("Действие первое", "Пойти в магазин"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));

        newTask1.setId(task1.getId());
        manager.updateTask(newTask1);
        assertEquals(newTask1, manager.getTask(newTask1.getId()));
    }

    @Test
    public void testAddTaskWithOverlapTime() {
        Task newTask1 = new Task("Действие первое", "Проверем пересечение где дата начала пересекается с периодом TASK1"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 11)
                , Duration.ofMinutes(30));
        Task newTask2 = new Task("Действие первое", "Проверем пересечение где дата окончания пересекается с периодом TASK1"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 9)
                , Duration.ofMinutes(30));
        Task newTask3 = new Task("Действие первое", "Проверяем пересечение, где NewTask3 лежит в периоде TASK1"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 11)
                , Duration.ofMinutes(5));
        Task newTask4 = new Task("Действие первое", "Проверяем пересечение, где TASK1 лежит в периоде NewTask4"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 8, 30)
                , Duration.ofMinutes(60));
        Task newTask5 = new Task("Действие первое", "Проверем пересечение дубль периода TASK"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));

        Exception e;

        e = assertThrows(ManagerOverlapTimeException.class, () -> {manager.addTask(newTask1);});
        e = assertThrows(ManagerOverlapTimeException.class, () -> {manager.addTask(newTask2);});
        e = assertThrows(ManagerOverlapTimeException.class, () -> {manager.addTask(newTask3);});
        e = assertThrows(ManagerOverlapTimeException.class, () -> {manager.addTask(newTask4);});
        e = assertThrows(ManagerOverlapTimeException.class, () -> {manager.addTask(newTask5);});
    }

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
        assertEquals(manager.getTask(task2.getId()), task2);
        manager.deleteTask(task2.getId());
        assertNull(manager.getTask(task2.getId()));
    }

    @Test
    public void testDeleteEpicWithoutSubtask() {
        assertEquals(manager.getEpic(epic2.getId()), epic2);
        manager.deleteEpic(epic2.getId());
        assertNull(manager.getEpic(epic2.getId()));

        Epic epicFromManager = null;
        assertTrue(epic2.getIdSubTask().isEmpty());

        for (SubTask subTask : manager.getAllSubTask()) {
            epicFromManager = manager.getEpic(subTask.getId());
        }
        assertNull(epicFromManager);
    }

    @Test
    public void testDeleteEpicWithtSubtask() {
        assertEquals(manager.getEpic(epic1.getId()), epic1);
        manager.deleteEpic(epic1.getId());
        assertNull(manager.getEpic(epic1.getId()));

        Epic epicFromManager = null;
        assertFalse(epic1.getIdSubTask().isEmpty());

        for (SubTask subTask : manager.getAllSubTask()) {
            epicFromManager = manager.getEpic(subTask.getId());
        }
        assertNull(epicFromManager);

        SubTask subTaskInManager = null;
        for (Integer subTaskId : epic1.getIdSubTask()) {
            subTaskInManager = manager.getSubTask(subTaskId);
            break;
        }
        assertNull(subTaskInManager);
    }

    @Test
    public void updateEpic() {
        Epic newEpic = new Epic("Поиграть в шахматы", "Обновленный эпик");

        newEpic.setId(epic2.getId());

        SubTask newSubTask = new SubTask("Перевести игру в эндшпиль"
                        , "Обновленная сабтаска"
                        , Status.DONE
                        , newEpic.getId()
                        , LocalDateTime.of(2021, 10, 21, 13, 5)
                        , Duration.ofMinutes(5));

        manager.updateEpic(newEpic);
        manager.addSubTask(newSubTask);

        Epic epicFromManager = manager.getEpic(newEpic.getId());
        assertEquals(epicFromManager.getDescription(), newEpic.getDescription());
        assertEquals(epicFromManager.getName(), newEpic.getName());
    }

    @Test
    public void testDeleteSubTask() {
        assertEquals(manager.getSubTask(subTask2.getId()), subTask2);
        manager.deleteSubTask(subTask2.getId());
        assertNull(manager.getSubTask(subTask2.getId()));
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

    @Test
    public void testUpdateSubTask() {
        SubTask newSubTask1 = new SubTask("Сделать испанскую защиту"
                , "Обновленная сабтаска"
                , Status.NEW
                , epic1.getId()
                , LocalDateTime.of(2020, 10, 21, 13, 1)
                , Duration.ofMinutes(3));

        newSubTask1.setId(newSubTask1.getId());
        manager.updateSubTask(newSubTask1);

        assertEquals(manager.getSubTask(newSubTask1.getId()), newSubTask1);
    }

    @Test
    public void testUpdateSubtaskWithChangeStartTimeThrowsException() {
        subTask1.setStartTime(LocalDateTime.of(2020, 10, 21, 12, 6));
        assertThrows(ManagerOverlapTimeException.class, () -> manager.updateSubTask(subTask1));
    }

    @Test
    public void testUpdateSubtaskWithChangeStartTimeNotThrowsException() {
        subTask1.setStartTime(LocalDateTime.of(2020, 10, 21, 12, 40));
        assertDoesNotThrow(() -> manager.updateSubTask(subTask1));
    }

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
        testSet.addAll(manager.getAllSubTask());

        List<Task> testList = new ArrayList<>(testSet);
        List<Task> listFromManager = manager.getPrioritatedTasks();

        assertEquals(testList, listFromManager);
        manager.clearTasks();
        manager.clearSubTasks();
        assertTrue(manager.getPrioritatedTasks().isEmpty());
    }
}