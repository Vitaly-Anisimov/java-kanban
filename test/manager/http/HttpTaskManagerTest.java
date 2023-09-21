package manager.http;

import manager.Managers;
import manager.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String TEST_URL = "http://localhost:" + KVServer.PORT;

    @Override
    public HttpTaskManager createTaskManager() {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    @Test
    public void testLoadWithoutEpic() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
        manager.clearEpics();
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
        kvServer.stop();
    }

    @Test
    public void testLoadWithHistory() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask1.getId());
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
        kvServer.stop();
    }

    @Test
    public void testLoadFromEmptyServer() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubTasks();
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
        kvServer.stop();
    }

    @Test
    public void testLoadWithoutEpicSubtask() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
        manager.clearSubTasks();
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
        kvServer.stop();
    }
}