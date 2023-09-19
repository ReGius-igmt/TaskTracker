package ru.practice.task.tracker.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practice.task.tracker.LocalDateTimeDeserializer;
import ru.practice.task.tracker.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public abstract class BaseHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).create();
    protected final TaskManager manager;

    public BaseHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    onGetRequest(exchange);
                    break;
                case "POST":
                    onPostRequest(exchange);
                    break;
                case "DELETE":
                    onDeleteRequest(exchange);
                    break;
                default:
                    onUnknownRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, e.getMessage());
        }
    }

    protected void onGetRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Method Not Allowed");
    }

    protected void onPostRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Method Not Allowed");
    }

    protected void onDeleteRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Method Not Allowed");
    }

    protected void onUnknownRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Method Not Allowed");
    }

    protected void sendResponse(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, 0);
    }

    protected void sendResponse(HttpExchange exchange, int status, String body, Object... args) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        sendResponse(exchange, status);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(String.format(body, args).getBytes());
        }
    }

    protected void sendResponse(HttpExchange exchange, String body, Object... args) throws IOException {
        sendResponse(exchange, 200, body, args);
    }

    protected void sendResponse(HttpExchange exchange, Object body) throws IOException {
        sendResponse(exchange, 200, body);
    }

    protected void sendResponse(HttpExchange exchange, int status, Object body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        sendResponse(exchange, status);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(gson.toJson(body).getBytes(StandardCharsets.UTF_8));
        }
    }

    protected <T> T getBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            return gson.fromJson(requestBody, clazz);
        } catch (Exception e) {
            sendResponse(exchange, 400, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
