package ru.practice.task.tracker.server;

import com.sun.net.httpserver.HttpServer;
import ru.practice.task.tracker.manager.Managers;
import ru.practice.task.tracker.manager.TaskManager;
import ru.practice.task.tracker.server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException {
        TaskManager manager = Managers.getFileBackend();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic", new EpicHandler(manager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(manager));
        httpServer.createContext("/tasks/subtask/epic", new SubtaskByEpicHandler(manager));
        httpServer.createContext("/tasks/history", new HistoryHandler(manager));
        httpServer.createContext("/tasks", new TasksHandler(manager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }
}
