package manager.http;

import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final String TEST_URL = "http://localhost:" + KVServer.PORT;
    private KVServer kvServer;

    @Override
    public HttpTaskManager createTaskManager() {
        return new HttpTaskManager("http://localhost:" + KVServer.PORT);
    }

    @BeforeEach
    public void setupTest() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void testLoadWithoutEpic() throws IOException {
        manager.clearEpics();
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
    }

    @Test
    public void testLoadWithHistory() throws IOException {
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
    }

    @Test
    public void testLoadFromEmptyServer() throws IOException {
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
    }

    @Test
    public void testLoadWithoutEpicSubtask() throws IOException {
        manager.clearSubTasks();
        manager.save();

        HttpTaskManager testmanager = HttpTaskManager.load(TEST_URL);

        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
        assertEquals(manager.getPrioritatedTasks().size(), testmanager.getPrioritatedTasks().size());
    }
}