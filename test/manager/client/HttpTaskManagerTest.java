package manager.client;

import manager.Managers;
import manager.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;
    @Override
    public HttpTaskManager createTaskManager() {
        return (HttpTaskManager) Managers.getDefault();
    }

    @AfterEach
    public void stopKVServer() {
        kvServer.stop();
    }
    @BeforeEach
    public void startKVServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        createTestTasks();
    }

    @Test
    public void testLoadWithoutEpic() {
        manager.clearEpics();
        manager.save();

        HttpTaskManager testmanager = (HttpTaskManager) Managers.getDefault();

        testmanager.load();
        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
    }

    @Test
    public void testLoadWithHistory() {
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask1.getId());
        manager.save();

        HttpTaskManager testHttpManager = (HttpTaskManager) Managers.getDefault();

        testHttpManager.load();
        assertEquals(manager.getAllTask().size(), testHttpManager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testHttpManager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testHttpManager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testHttpManager.getHistory().size());
    }

    @Test
    public void testLoadFromEmptyServer() {
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubTasks();
        manager.save();

        HttpTaskManager testmanager = (HttpTaskManager) Managers.getDefault();

        testmanager.load();
        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
    }

    @Test
    public void testLoadWithoutEpicSubtask() {
        manager.clearSubTasks();
        manager.save();

        HttpTaskManager testmanager = (HttpTaskManager) Managers.getDefault();

        testmanager.load();
        assertEquals(manager.getAllTask().size(), testmanager.getAllTask().size());
        assertEquals(manager.getAllEpic().size(), testmanager.getAllEpic().size());
        assertEquals(manager.getAllSubTask().size(), testmanager.getAllSubTask().size());
        assertEquals(manager.getHistory().size(), testmanager.getHistory().size());
    }
}