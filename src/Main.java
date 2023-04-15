import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        SubTask subTask;
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Первая задача", "Просто задача", TaskStatus.DONE);
        Epic epic = new Epic("Утренние дела", "Дела которые нужно делать утром");
        taskManager.addEpic(epic);
        subTask = new SubTask(2, "Открыть глаза", "Первое действие", TaskStatus.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(3, "Встать с кровати", "Второе действие", TaskStatus.NEW, epic.getId());
        taskManager.addSubTask(subTask);
        subTask = new SubTask(4, "Пойти умыться", "Третье действие", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.addSubTask(subTask);

        SubTask subTask4 = new SubTask(2, "ОДИН", "Первое действие", TaskStatus.NEW, epic.getId());
        taskManager.updateSubTask(subTask4);
        SubTask subTask5 = new SubTask(3, "ДВА", "Второе действие", TaskStatus.IN_PROGRESS, epic.getId());
        taskManager.updateSubTask(subTask5);

        ArrayList<Epic> epics = taskManager.getAllEpic();
        for (int i = 0; i < epics.size(); i++) {
            System.out.println(epics.get(i).toString());
        }

        ArrayList<SubTask> subTasks = taskManager.getAllSubTask();
        for (int i = 0; i < subTasks.size(); i++) {
            System.out.println(subTasks.get(i).toString());
        }
    }
}
