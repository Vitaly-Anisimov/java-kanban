import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        SubTask subTask;
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Первая задача", "Просто задача", Status.DONE);
        Epic epic = new Epic("Утренние дела", "Дела которые нужно делать утром");
        taskManager.addEpic(epic);
        subTask = new SubTask(2, "Открыть глаза", "Первое действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(3, "Встать с кровати", "Второе действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(4, "Пойти умыться", "Третье действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(5, "Заварить кофе", "Четвертое действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(6, "Пожарить яичницу", "Пятое действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(7, "Позавтракать", "Шестое действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(8, "Покурить", "Седьмое действие", Status.NEW, epic.getId());
        taskManager.addSubTask(subTask);

        subTask = taskManager.getSubTask(1);
        subTask = taskManager.getSubTask(2);
        subTask = taskManager.getSubTask(3);
        subTask = taskManager.getSubTask(4);
        subTask = taskManager.getSubTask(5);
        subTask = taskManager.getSubTask(6);
        subTask = taskManager.getSubTask(7);
        subTask = taskManager.getSubTask(8);
        subTask = taskManager.getSubTask(7);
        subTask = taskManager.getSubTask(6);
        subTask = taskManager.getSubTask(5);
        subTask = taskManager.getSubTask(4);

        for (Task task1 : taskManager.getHistory()) {
            System.out.println(task1.toString());
        }

    }
}