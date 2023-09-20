package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.HttpTaskServerNotFoundException;
import exception.HttpTaskServerUnsupportMethodException;
import manager.Managers;
import manager.TaskManager;
import manager.client.adapters.GsonFormatBuilder;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    private void handleForTask(HttpExchange exchange, String requestMethod, String paramQuery, Integer idFromRequest) throws IOException, HttpTaskServerNotFoundException, HttpTaskServerUnsupportMethodException {
        Task taskFromManager;
        Task taskFromBody;

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    sendText(exchange, formatToGson(manager.getAllTask()), 200);
                    return;
                } else {
                    taskFromManager = manager.getTask(idFromRequest.intValue());

                    if (taskFromManager == null) {
                        throw new HttpTaskServerNotFoundException("Not get task id = " + paramQuery);
                    } else {
                        sendText(exchange, formatToGson(taskFromManager), 200);
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);

                taskFromBody = gson.fromJson(requestBody, Task.class);
                taskFromManager = manager.getTask(taskFromBody.getId());

                if (taskFromManager == null) {
                    manager.addTask(taskFromBody);
                    sendText(exchange, formatToGson(taskFromBody), 200);
                } else {
                    manager.updateTask(taskFromBody);
                    sendText(exchange, String.valueOf(taskFromBody.getId()), 201);
                }

                break;
            case "DELETE":
                if (idFromRequest == null) {
                    throw new HttpTaskServerNotFoundException("id for delete is null");
                }

                manager.deleteTask(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), 202);

                break;
            default:
                throw new HttpTaskServerUnsupportMethodException("Unsupported method");
        }
    }

    private void handleForEpic(HttpExchange exchange, String requestMethod, String paramQuery, Integer idFromRequest) throws IOException, HttpTaskServerNotFoundException, HttpTaskServerUnsupportMethodException {
        Epic epicFromManager;
        Epic epicFromBody;

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    sendText(exchange, formatToGson(manager.getAllEpic()), 200);
                    return;
                } else {
                    epicFromManager = manager.getEpic(idFromRequest.intValue());

                    if (epicFromManager == null) {
                        throw new HttpTaskServerNotFoundException("Not get epic id = " + paramQuery);
                    } else {
                        sendText(exchange, formatToGson(epicFromManager), 200);
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);

                epicFromBody = gson.fromJson(requestBody, Epic.class);
                epicFromManager = manager.getEpic(epicFromBody.getId());

                if (epicFromManager == null) {
                    manager.addEpic(epicFromBody);
                    sendText(exchange, formatToGson(epicFromBody), 200);
                } else {
                    manager.updateEpic(epicFromBody);
                    sendText(exchange, formatToGson(epicFromBody), 201);
                }

                break;
            case "DELETE":
                if (idFromRequest == null) {
                    throw new HttpTaskServerNotFoundException("id for delete is null");
                }

                manager.deleteEpic(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), 202);

                break;
            default:
                throw new HttpTaskServerUnsupportMethodException("Unsupported method");
        }
    }

    private void handleForSubTask(HttpExchange exchange, String requestMethod, String paramQuery, Integer idFromRequest) throws IOException, HttpTaskServerNotFoundException, HttpTaskServerUnsupportMethodException {
        SubTask subTaskFromManager;
        SubTask subTaskFromBody;

        switch (requestMethod) {
            case "GET":
                if (paramQuery == null) {
                    sendText(exchange, formatToGson(manager.getAllSubTask()), 200);
                    return;
                } else {
                    subTaskFromManager = manager.getSubTask(idFromRequest.intValue());

                    if (subTaskFromManager == null) {
                        throw new HttpTaskServerNotFoundException("Not get subTask id = " + paramQuery);
                    } else {
                        sendText(exchange, formatToGson(subTaskFromManager), 200);
                    }
                }
                break;
            case "POST":
                String requestBody = readText(exchange);

                subTaskFromBody = gson.fromJson(requestBody, SubTask.class);
                subTaskFromManager = manager.getSubTask(subTaskFromBody.getId());

                if (subTaskFromManager == null) {
                    manager.addSubTask(subTaskFromBody);
                    sendText(exchange, formatToGson(subTaskFromBody), 200);
                } else {
                    manager.updateSubTask(subTaskFromBody);
                    sendText(exchange, String.valueOf(subTaskFromBody.getId()), 201);
                }

                break;
            case "DELETE":
                subTaskFromManager = manager.getSubTask(idFromRequest);

                if (idFromRequest == null) {
                    throw new HttpTaskServerNotFoundException("id for delete is null");
                }

                manager.deleteSubTask(idFromRequest);
                sendText(exchange, formatToGson(idFromRequest), 202);

                break;
            default:
                throw new HttpTaskServerUnsupportMethodException("Unsupported method");
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
                        sendText(exchange, formatToGson(manager.getPrioritatedTasks()), 200);
                    } else {
                        throw new HttpTaskServerUnsupportMethodException("Not supported method");
                    }
                    break;
                case "history/":
                    if (exchange.getRequestMethod().equals("GET")) {
                        sendText(exchange, formatToGson(manager.getHistory()), 200);
                    } else {
                        throw new HttpTaskServerUnsupportMethodException("Not supported method");
                    }
                    break;
                case "task/":
                    handleForTask(exchange, requestMethod, paramQuery, idFromRequest);
                    break;
                case "epic/":
                    handleForEpic(exchange, requestMethod, paramQuery, idFromRequest);
                    break;
                case "subtask/":
                    handleForSubTask(exchange, requestMethod, paramQuery, idFromRequest);
                    break;
                default:
                    throw new HttpTaskServerUnsupportMethodException("Unsupported URL");
            }
        } catch (IOException | NumberFormatException | HttpTaskServerNotFoundException |
                 HttpTaskServerUnsupportMethodException e) {
            sendText(exchange, formatToGson(e.getMessage()), 404);
        } finally {
            exchange.close();
        }
    }
}
