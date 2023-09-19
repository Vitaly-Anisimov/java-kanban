package server;

import com.google.gson.Gson;
import manager.client.adapters.GsonFormatBuilder;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static String URL_NAME = "http://localhost:" + HttpTaskServer.HTTP_TASK_SERVER_PORT + "/tasks/";
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Epic epic1;
    private Epic epic2;
    private Epic epic3;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;
    private SubTask subTask4;

    KVServer kvServer;

    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;

    private Gson gson;

    public HttpResponse<String> createGetQuery(String param) throws IOException, InterruptedException {
        URI uri = URI.create(URL_NAME + param);

        HttpRequest request = HttpRequest
                        .newBuilder()
                        .GET()
                        .uri(uri)
                        .header("Accept", "application/json")
                        .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> createDeleteQuery(String param) throws IOException, InterruptedException {
        URI uri = URI.create(URL_NAME + param);

        HttpRequest request = HttpRequest
                        .newBuilder()
                        .DELETE()
                        .uri(uri)
                        .header("Accept", "application/json")
                        .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> createCreateOrUpdateQuery(Task task, String param) throws IOException, InterruptedException {
        URI uri = URI.create(URL_NAME + param);
        String query = gson.toJson(task);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(query);
        HttpRequest request = HttpRequest
                        .newBuilder()
                        .POST(body)
                        .uri(uri)
                        .header("Accept", "application/json")
                        .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void createTasks() throws IOException, InterruptedException {
        HttpResponse<String> response;

        task1 = new Task("Действие первое", "Пойти в магазин"
                , Status.NEW, LocalDateTime.of(2010, 8, 5, 9, 10)
                , Duration.ofMinutes(30));
        task2 = new Task("Действие второе", "Купить иранскую колу"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 40)
                , Duration.ofMinutes(5));
        task3 = new Task("Действие третье", "тест"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 8, 5, 10, 50)
                , Duration.ofMinutes(10));
        task4 = new Task("Действие четвертое", "тест"
                , Status.IN_PROGRESS, LocalDateTime.of(2010, 9, 5, 10, 50)
                , Duration.ofMinutes(10));

        response = createCreateOrUpdateQuery(task1, "task/");
        Integer taskFromServer1 = gson.fromJson(response.body(), Integer.class);

        task1.setId(taskFromServer1.intValue());
        response = createCreateOrUpdateQuery(task2, "task/");
        Integer taskFromServer2 = gson.fromJson(response.body(), Integer.class);

        task2.setId(taskFromServer2.intValue());
        response = createCreateOrUpdateQuery(task3, "task/");
        Integer taskFromServer3 = gson.fromJson(response.body(), Integer.class);

        task3.setId(taskFromServer3.intValue());

        epic1 = new Epic("Поиграть в шахматы", "Поставить мат Магнусуну");
        epic2 = new Epic("Разгадать смысл жизни", "Подумать зачем всё это надо");
        epic3 = new Epic("Третий тестовый эпик", "Не нужный эпик");
        epic3.setId(9);

        response = createCreateOrUpdateQuery(epic1, "epic/");
        Integer epicFromServer1 = gson.fromJson(response.body(), Integer.class);

        epic1.setId(epicFromServer1.intValue());
        response = createCreateOrUpdateQuery(epic2, "epic/");
        Integer epicFromServer2 = gson.fromJson(response.body(), Integer.class);

        epic2.setId(epicFromServer2.intValue());

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

        subTask4 = new SubTask("Четвертая сабтаска"
                , "тест"
                , Status.IN_PROGRESS
                , epic2.getId()
                , LocalDateTime.of(2020, 10, 21, 14, 20)
                , Duration.ofMinutes(20));

        response = createCreateOrUpdateQuery(subTask1, "subtask/");
        Integer subTaskFromServer1 = gson.fromJson(response.body(), Integer.class);

        subTask1.setId(subTaskFromServer1.intValue());
        response = createCreateOrUpdateQuery(subTask2, "subtask/");
        Integer subTaskFromServer2 = gson.fromJson(response.body(), Integer.class);

        subTask2.setId(subTaskFromServer2.intValue());
        response = createCreateOrUpdateQuery(subTask3, "subtask/");
        Integer subTaskFromServer3 = gson.fromJson(response.body(), Integer.class);

        subTask3.setId(subTaskFromServer3.intValue());
    }


    @BeforeEach
    public void settupTest() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        gson = GsonFormatBuilder.buildGson();
        httpClient = HttpClient.newHttpClient();

        createTasks();
    }

    @AfterEach
    public void stopServers() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    public void testGetAllPrioritizedTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("");
        ArrayList<Task> tasks = gson.fromJson(response.body(), ArrayList.class);

        assertEquals(6, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNegativeGetAllPrioritizedTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createCreateOrUpdateQuery(task1, "");

        assertEquals(400, response.statusCode());
        response = createDeleteQuery("");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("task/?id=" + task1.getId());

        response = createGetQuery("task/?id=" + task2.getId());
        response = createGetQuery("history/");

        ArrayList<Task> tasks = gson.fromJson(response.body(), ArrayList.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("history/");
        assertEquals(404, response.statusCode());
        response = createDeleteQuery("history/");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testNegativeGetHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("history/");

        assertEquals(400, response.statusCode());
        response = createCreateOrUpdateQuery(task1, "history/");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("task/");
        ArrayList<Task> tasks = gson.fromJson(response.body(), ArrayList.class);

        assertEquals(3, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNegativeGetAllTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("task/");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createCreateOrUpdateQuery(task4, "task/");

        assertEquals(200, response.statusCode());

        Integer testTaskId = gson.fromJson(response.body(), Integer.class);

        task4.setId(testTaskId.intValue());
        response = createGetQuery("task/?id="+ task4.getId());

        Task testTask = gson.fromJson(response.body(), Task.class);

        assertTrue(task4.equals(testTask));
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        task2.setName("Тест junit");
        task2.setDescription("Попробуем");

        HttpResponse<String> response = createCreateOrUpdateQuery(task2, "task/");

        assertEquals(201, response.statusCode());

        Integer testTaskId = gson.fromJson(response.body(), Integer.class);

        assertEquals(testTaskId.intValue(), task2.getId());
        response = createGetQuery("task/?id="+ task2.getId());

        Task testTask = gson.fromJson(response.body(), Task.class);

        assertTrue(task2.equals(testTask));
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("task/?id=" + task1.getId());
        assertEquals(202, response.statusCode());

        response = createGetQuery("task/?id=" + task1.getId());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestGetTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("task/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestDeleteTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("task/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("epic/");
        ArrayList<Epic> epics = gson.fromJson(response.body(), ArrayList.class);

        assertEquals(2, epics.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNegativeGetAllEpics() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("epic/");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = createCreateOrUpdateQuery(epic3, "epic/");

        assertEquals(200, response.statusCode());

        Integer testEpicId = gson.fromJson(response.body(), Integer.class);

        epic3.setId(testEpicId.intValue());
        response = createGetQuery("epic/?id="+ epic3.getId());

        Epic testEpic = gson.fromJson(response.body(), Epic.class);

        assertTrue(epic3.equals(testEpic));
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        epic2.setName("Тест junit");
        epic2.setDescription("Попробуем");

        HttpResponse<String> response = createCreateOrUpdateQuery(epic2, "epic/");

        assertEquals(201, response.statusCode());

        Integer testEpicId = gson.fromJson(response.body(), Integer.class);
        epic2.setId(testEpicId);

        response = createGetQuery("epic/?id="+ epic2.getId());

        Epic testEpic = gson.fromJson(response.body(), Epic.class);

        assertTrue(epic2.equals(testEpic));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("epic/?id=" + epic1.getId());
        assertEquals(202, response.statusCode());

        response = createGetQuery("epic/?id=" + epic1.getId());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestGetEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("epic/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestDeleteEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("epic/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("subtask/");
        ArrayList<SubTask> subTasks = gson.fromJson(response.body(), ArrayList.class);

        assertEquals(3, subTasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNegativeGetAllSubTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("subTask/");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createCreateOrUpdateQuery(subTask4, "subtask/");

        assertEquals(200, response.statusCode());

        Integer testSubTaskId = gson.fromJson(response.body(), Integer.class);

        subTask4.setId(testSubTaskId.intValue());
        response = createGetQuery("subtask/?id="+ subTask4.getId());

        SubTask testSubTask = gson.fromJson(response.body(), SubTask.class);

        assertTrue(subTask4.equals(testSubTask));
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        subTask2.setName("Тест junit");
        subTask2.setDescription("Попробуем");

        HttpResponse<String> response = createCreateOrUpdateQuery(subTask2, "subtask/");

        assertEquals(201, response.statusCode());

        Integer testSubTaskId = gson.fromJson(response.body(), Integer.class);
        subTask2.setId(testSubTaskId);

        response = createGetQuery("subtask/?id="+ subTask2.getId());

        SubTask testSubTask = gson.fromJson(response.body(), SubTask.class);

        assertTrue(subTask2.equals(testSubTask));
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createDeleteQuery("subtask/?id=" + subTask1.getId());
        assertEquals(202, response.statusCode());

        response = createGetQuery("subtask/?id=" + subTask1.getId());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestGetSubTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("subtask/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestDeleteSubTask() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("subtask/?id=100");
        assertEquals(400, response.statusCode());
    }

    @Test
    public void negativeTestNotCorrectUrl() throws IOException, InterruptedException {
        HttpResponse<String> response = createGetQuery("subtask1231231231/?id=100");
        assertEquals(400, response.statusCode());
    }


}