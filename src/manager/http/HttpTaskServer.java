package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.BadMethodException;
import exception.NotFoundException;
import manager.TaskManager;
import manager.http.adapters.GsonFormatBuilder;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    public static final int HTTP_TASK_SERVER_PORT = 8080;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager manager;
    private final HttpServer server;
    private final Gson gson;


    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
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

    private void handleForTask(HttpExchange exchange, String requestMethod, Integer idFromRequest) throws IOException, NotFoundException {
        Task taskFromManager;
        Task taskFromBody;

        switch (requestMethod) {
            case "GET":
                if (idFromRequest == null) {
                    sendText(exchange, formatToGson(manager.getAllTask()), HttpURLConnection.HTTP_OK);
                    return;
                } else {
                    taskFromManager = manager.getTask(idFromRequest);
                    sendText(exchange, formatToGson(taskFromManager), HttpURLConnection.HTTP_OK);
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                int statusCode;

                taskFromBody = gson.fromJson(requestBody, Task.class);

                if (taskFromBody.getId() == 0) {
                    manager.addTask(taskFromBody);
                    statusCode = HttpURLConnection.HTTP_CREATED;
                } else {
                    manager.updateTask(taskFromBody);
                    statusCode = HttpURLConnection.HTTP_OK;
                }

                sendText(exchange, formatToGson(taskFromBody), statusCode);
                break;
            case "DELETE":
                if (idFromRequest == null) {
                    throw new BadMethodException("Need id task for delete");
                }

                manager.deleteTask(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), HttpURLConnection.HTTP_OK);

                break;
            default:
                sendText(exchange, formatToGson("Wait GET/POST/DELETE method, expected : " + requestMethod), HttpURLConnection.HTTP_BAD_METHOD);
        }
    }

    private void handleForEpic(HttpExchange exchange, String requestMethod, Integer idFromRequest) throws IOException, NotFoundException {
        Epic epicFromManager;
        Epic epicFromBody;

        switch (requestMethod) {
            case "GET":
                if (idFromRequest == null) {
                    sendText(exchange, formatToGson(manager.getAllEpic()), HttpURLConnection.HTTP_OK);
                    return;
                } else {
                    epicFromManager = manager.getEpic(idFromRequest);
                    sendText(exchange, formatToGson(epicFromManager), HttpURLConnection.HTTP_OK);
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                int statusCode;

                epicFromBody = gson.fromJson(requestBody, Epic.class);

                if (epicFromBody.getId() == 0) {
                    manager.addEpic(epicFromBody);
                    statusCode = HttpURLConnection.HTTP_CREATED;
                } else {
                    manager.updateEpic(epicFromBody);
                    statusCode = HttpURLConnection.HTTP_OK;
                }

                sendText(exchange, formatToGson(epicFromBody), statusCode);
                break;
            case "DELETE":
                if (idFromRequest == null) {
                    throw new BadMethodException("Need id task for delete");
                }

                manager.deleteEpic(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), HttpURLConnection.HTTP_OK);

                break;
            default:
                sendText(exchange, formatToGson("Wait GET/POST/DELETE method, expected : " + requestMethod), HttpURLConnection.HTTP_BAD_METHOD);
        }
    }

    private void handleForSubTask(HttpExchange exchange, String requestMethod, Integer idFromRequest) throws IOException, NotFoundException {
        SubTask subTaskFromManager;
        SubTask subTaskFromBody;

        switch (requestMethod) {
            case "GET":
                if (idFromRequest == null) {
                    sendText(exchange, formatToGson(manager.getAllSubTask()), HttpURLConnection.HTTP_OK);
                    return;
                } else {
                    subTaskFromManager = manager.getSubTask(idFromRequest);
                    sendText(exchange, formatToGson(subTaskFromManager), HttpURLConnection.HTTP_OK);
                }
                break;
            case "POST":
                String requestBody = readText(exchange);
                int statusCode;

                subTaskFromBody = gson.fromJson(requestBody, SubTask.class);

                if (subTaskFromBody.getId() == 0) {
                    manager.addSubTask(subTaskFromBody);
                    statusCode = HttpURLConnection.HTTP_CREATED;
                } else {
                    manager.updateSubTask(subTaskFromBody);
                    statusCode = HttpURLConnection.HTTP_OK;
                }

                sendText(exchange, formatToGson(subTaskFromBody), statusCode);
                break;
            case "DELETE":
                if (idFromRequest == null) {
                    throw new BadMethodException("Need id task for delete");
                }

                manager.deleteSubTask(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), HttpURLConnection.HTTP_OK);

                break;
            default:
                sendText(exchange, formatToGson("Wait GET/POST/DELETE method, expected : " + requestMethod), HttpURLConnection.HTTP_BAD_METHOD);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceFirst("/tasks/", "");
        String paramQuery = exchange.getRequestURI().getQuery();
        String requestMethod = exchange.getRequestMethod();
        Integer idFromRequest = null;

        try {
            if (!(paramQuery == null)) {
                idFromRequest = Integer.parseInt(paramQuery.replaceFirst("id=", ""));
            }

            switch (path) {
                case "":
                    if (exchange.getRequestMethod().equals("GET")) {
                        sendText(exchange, formatToGson(manager.getPrioritatedTasks()), HttpURLConnection.HTTP_OK);
                    } else {
                        throw new BadMethodException("Unsupported method : " + exchange.getRequestMethod());
                    }
                    break;
                case "history/":
                    if (exchange.getRequestMethod().equals("GET")) {
                        sendText(exchange, formatToGson(manager.getHistory()), HttpURLConnection.HTTP_OK);
                    } else {
                        throw new BadMethodException("Unsupported method : " + exchange.getRequestMethod());
                    }
                    break;
                case "task/":
                    handleForTask(exchange, requestMethod, idFromRequest);
                    break;
                case "epic/":
                    handleForEpic(exchange, requestMethod, idFromRequest);
                    break;
                case "subtask/":
                    handleForSubTask(exchange, requestMethod, idFromRequest);
                    break;
                default:
                    throw new BadMethodException("Unsupported URL : " + exchange.getRequestURI());
            }
        } catch (NumberFormatException e) {
            sendText(exchange, formatToGson(e.getMessage()), HttpURLConnection.HTTP_BAD_REQUEST);
        } catch (NotFoundException e) {
            sendText(exchange, formatToGson(e.getMessage()), HttpURLConnection.HTTP_NOT_FOUND);
        } catch (BadMethodException e) {
            sendText(exchange, formatToGson(e.getMessage()), HttpURLConnection.HTTP_BAD_METHOD);
        } finally {
            exchange.close();
        }
    }
}
