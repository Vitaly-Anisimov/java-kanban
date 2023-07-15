package managers.taskManager;
class InMemoryTaskManagerTest extends TaskManagerTest {
    TaskManager manager = Managers.getDefault();

    @Override
    TaskManager createTaskManager() {
        return manager;
    }
}