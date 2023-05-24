import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Действие первое", "Пойти в магазин", Status.NEW);
        Task task2 = new Task("Действие второе", "Купить иранскую колу", Status.IN_PROGRESS);
        Task task3 = new Task("Действие третье", "Купить шаурму", Status.DONE);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        Epic epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");
        Epic epic2 = new Epic("Разгадать смысл жизни", "Подумать зачем всё это надо");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        SubTask subTask1 = new SubTask("Сделать испанскую защиту", "Выдвинуть 3 пешки и 1 коня", Status.DONE, epic1.getId());
        SubTask subTask2 = new SubTask("Перевести игру в эндшпиль", "Вытащить на середину ферзя", Status.DONE, epic1.getId());
        SubTask subTask3 = new SubTask("Проиграть партию", "Предложить сдаться", Status.IN_PROGRESS, epic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        //Requests
        Epic epic = taskManager.getEpic(epic1.getId());
        epic = taskManager.getEpic(epic1.getId());
        epic = taskManager.getEpic(epic2.getId());
        SubTask subTask = taskManager.getSubTask(subTask3.getId());
        subTask = taskManager.getSubTask(subTask2.getId());
        subTask = taskManager.getSubTask(subTask1.getId());
        Task task = taskManager.getTask(task1.getId());
        task = taskManager.getTask(task1.getId());
        taskManager.deleteTask(task1.getId());
        task = taskManager.getTask(task2.getId());
        taskManager.clearEpics();
        printHistory(taskManager);
    }

    static void printHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();
        System.out.println("Количество записей в истории = " + history.size());
        for (Task task : history) {
            System.out.println(task.toString());
        }
    }


}