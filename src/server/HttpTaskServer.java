package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import manager.client.adapters.GsonFormatBuilder;
import manager.file.CSVTaskFormat;
import manager.file.FileBackedTasksManager;
import model.Epic;
import model.SubTask;
import model.Task;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {
    public static final int HTTP_TASK_SERVER_PORT = 8080;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager manager;
    private final HttpServer server;
    private final Gson gson;


    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault();
        gson = GsonFormatBuilder.buildGson();
        server = HttpServer.create(new InetSocketAddress(HTTP_TASK_SERVER_PORT), 0);

        server.createContext("/tasks/", this::handle);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(DEFAULT_CHARSET);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().write(bytes);
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private <T> String formatToGson(T toGson) {
        return gson.toJson(toGson);
    }

    private Optional<Integer> parseId(String str) {
        Optional<Integer> result;

        try {
            result = Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            result = Optional.empty();
        }
        return result;
    }

    private void handleForTask(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String paramQuery = exchange.getRequestURI().getQuery();

        if (!(paramQuery == null)) {
            paramQuery = paramQuery.replaceFirst("id=", "");
        }

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    if (manager.getAllTask().isEmpty()) {
                        sendText(exchange, "Tasks is empty", 404);
                    } else {
                        sendText(exchange, formatToGson(manager.getAllTask()), 200);
                    }
                } else {
                    Optional<Integer> parsedId = parseId(paramQuery);

                    if (parsedId.isEmpty()) {
                        sendText(exchange, "Not correct query", 400);
                    } else {
                        Task taskFromManager = manager.getTask(parsedId.get());

                        if (taskFromManager == null) {
                            sendText(exchange, "Not found task with id = " + parsedId.get(), 400);
                        } else {
                            sendText(exchange, formatToGson(taskFromManager), 200);
                        }
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                if (requestBody.isBlank() || requestBody.isEmpty()) {
                    sendText(exchange, "Empty body request", 400);
                } else {
                    Task taskFromBody = gson.fromJson(requestBody, Task.class);
                    Task taskFromManager = manager.getTask(taskFromBody.getId());

                    if (taskFromManager == null) {
                        manager.addTask(taskFromBody);
                        sendText(exchange, String.valueOf(taskFromBody.getId()), 200);
                    } else {
                        manager.updateTask(taskFromBody);
                        sendText(exchange, String.valueOf(taskFromBody.getId()), 201);
                    }
                }
                break;
            case "DELETE":
                Optional<Integer> parsedId = parseId(paramQuery);

                if (parsedId.isEmpty()) {
                    sendText(exchange, "Not correct query", 400);
                } else {
                    Task taskFromManager = manager.getTask(parsedId.get());

                    if (taskFromManager == null) {
                        sendText(exchange, "Not found task with id = " + parsedId.get(), 400);
                    } else {
                        sendText(exchange, "Task was delete with id = " + taskFromManager.getId(), 202);
                        manager.deleteTask(taskFromManager.getId());
                    }
                }
                break;
            default:
                sendText(exchange, "Unsupported method", 405);
        }
    }

    private void handleForSubtask(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String paramQuery = exchange.getRequestURI().getQuery();

        if (!(paramQuery == null)) {
            paramQuery = paramQuery.replaceFirst("id=", "");
        }

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    if (manager.getAllSubTask().isEmpty()) {
                        sendText(exchange, "Subtasks is empty", 404);
                    } else {
                        sendText(exchange, formatToGson(manager.getAllSubTask()), 200);
                    }
                } else {
                    Optional<Integer> parsedId = parseId(paramQuery);

                    if (parsedId.isEmpty()) {
                        sendText(exchange, "Not correct query", 400);
                    } else {
                        SubTask subTaskFromManager = manager.getSubTask(parsedId.get());

                        if (subTaskFromManager == null) {
                            sendText(exchange, "Not found subtask with id = " + parsedId.get(), 400);
                        } else {
                            sendText(exchange, formatToGson(subTaskFromManager), 200);
                        }
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                if (requestBody.isBlank() || requestBody.isEmpty()) {
                    sendText(exchange, "Empty body request", 400);
                } else {
                    SubTask subTaskFromBody = gson.fromJson(requestBody, SubTask.class);
                    SubTask subTaskManager = manager.getSubTask(subTaskFromBody.getId());

                    if (subTaskManager == null) {
                        manager.addSubTask(subTaskFromBody);
                        sendText(exchange, String.valueOf(subTaskFromBody.getId()), 200);
                    } else {
                        manager.addSubTask(subTaskFromBody);
                        sendText(exchange, String.valueOf(subTaskFromBody.getId()), 201);
                    }
                }
                break;
            case "DELETE":
                Optional<Integer> parsedId = parseId(paramQuery);

                if (parsedId.isEmpty()) {
                    sendText(exchange, "Not correct query", 400);
                } else {
                    SubTask subTaskFromManager = manager.getSubTask(parsedId.get());

                    if (subTaskFromManager == null) {
                        sendText(exchange, "Not found subtask with id = " + parsedId.get(), 400);
                    } else {
                        manager.deleteSubTask(subTaskFromManager.getId());
                        sendText(exchange, "Subtask was delete with id = " + subTaskFromManager.getId(), 202);
                    }
                }
                break;
            default:
                sendText(exchange, "Unsupported method", 405);
        }
    }

    private void handleForEpic(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String paramQuery = exchange.getRequestURI().getQuery();

        if (!(paramQuery == null)) {
            paramQuery = paramQuery.replaceFirst("id=", "");
        }

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    if (manager.getAllEpic().isEmpty()) {
                        sendText(exchange, "Epics is empty", 404);
                    } else {
                        sendText(exchange, formatToGson(manager.getAllEpic()), 200);
                    }
                } else {
                    Optional<Integer> parsedId = parseId(paramQuery);

                    if (parsedId.isEmpty()) {
                        sendText(exchange, "Not correct query", 400);
                    } else {
                        Epic epicFromManager = manager.getEpic(parsedId.get());

                        if (epicFromManager == null) {
                            sendText(exchange, "Not found epic with id = " + parsedId.get(), 400);
                        } else {
                            sendText(exchange, formatToGson(epicFromManager), 200);
                        }
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                if (requestBody.isBlank() || requestBody.isEmpty()) {
                    sendText(exchange, "Empty body request", 400);
                } else {
                    Epic epicFromBody = gson.fromJson(requestBody, Epic.class);
                    Epic epicFromManager = manager.getEpic(epicFromBody.getId());

                    if (epicFromManager == null) {
                        manager.addEpic(epicFromBody);
                        sendText(exchange, String.valueOf(epicFromBody.getId()), 200);
                    } else {
                        manager.addEpic(epicFromBody);
                        sendText(exchange, String.valueOf(epicFromBody.getId()), 201);
                    }
                }
                break;
            case "DELETE":
                Optional<Integer> parsedId = parseId(paramQuery);

                if (parsedId.isEmpty()) {
                    sendText(exchange, "Not correct query", 400);
                } else {
                    Epic epicFromManager = manager.getEpic(parsedId.get());

                    if (epicFromManager == null) {
                        sendText(exchange, "Not found epic with id = " + parsedId.get(), 400);
                    } else {
                        manager.deleteEpic(epicFromManager.getId());
                        sendText(exchange, "Epic was delete with id = " + epicFromManager.getId(), 202);
                    }
                }
                break;
            default:
                sendText(exchange, "Unsupported method", 405);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst("/tasks/", "");

        switch (path) {
            case "":
                if (exchange.getRequestMethod().equals("GET")) {
                    if (manager.getPrioritatedTasks().isEmpty()) {
                        sendText(exchange, "PrioritatedTask is empty", 404);
                    } else {
                        sendText(exchange, formatToGson(manager.getPrioritatedTasks()), 200);
                    }
                } else {
                    sendText(exchange, "Not support", 400);
                }
                break;
            case "history/":
                if (exchange.getRequestMethod().equals("GET")) {
                    if (manager.getHistory().isEmpty()) {

                        sendText(exchange, "History is empty", 404);
                    } else {
                        sendText(exchange, formatToGson(manager.getHistory()), 200);
                    }
                } else {
                    sendText(exchange, "Not support", 400);
                }
                break;
            case "task/":
                handleForTask(exchange);
                break;
            case "epic/":
                handleForEpic(exchange);
                break;
            case "subtask/":
                handleForSubtask(exchange);
                break;
            default:
                sendText(exchange, "Not support", 400);
        }

        exchange.close();
    }
}
